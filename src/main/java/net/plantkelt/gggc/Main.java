/*
 * GGGC (c) 2011, 2012, Laurent Grégoire <laurent.gregoire@gmail.com>
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur
 * approbation par la Commission européenne - versions
 * ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que
 * conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse
 * suivante:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Sauf obligation légale ou contractuelle écrite, le
 * logiciel distribué sous la Licence est distribué «en
 * l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT,
 * expresses ou implicites.
 * Consultez la Licence pour les autorisations et les
 * restrictions linguistiques spécifiques relevant de la
 * Licence.
 */

package net.plantkelt.gggc;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;

/**
 * 
 */
public class Main {

	public static void main(String[] args) throws Exception {

		System.out
				.println(String
						.format("GGGC version %s - Copyright (c) 2011, 2012 Laurent Grégoire <laurent.gregoire@gmail.com>",
								getVersion()));
		System.out
				.println("Concédée sous licence EUPL, version 1.1 ou ultérieure.");
		System.out
				.println("Licensed under the EUPL, Version 1.1 or subsequent version.");

		if (args.length == 0)
			throw new IllegalArgumentException(
					"Usage: gggc <config.conf> ...");

		LayoutParameters layoutParams = new LayoutParameters();
		Map<String, CodeSequence> codes = new HashMap<String, CodeSequence>();
		Map<String, TextToEncode> texts = new HashMap<String, TextToEncode>();

		System.out.println("Loading configuration (using UTF8 encoding)...");
		for (String arg : args) {
			System.out.println("Loading '" + arg + "'...");
			Wini wini = new Wini(new InputStreamReader(
					new FileInputStream(arg), "UTF8"));
			Section layout = wini.get("layout");
			if (layout != null) {
				layoutParams.setCellWidth(layout.get("cell.width",
						Integer.class, layoutParams.getCellWidth()));
				layoutParams.setCellHeight(layout.get("cell.height",
						Integer.class, layoutParams.getCellHeight()));
				layoutParams.setColumns(layout.get("columns", Integer.class,
						layoutParams.getColumns()));
				layoutParams.setHeaderHeight(layout.get("header.height",
						Integer.class, layoutParams.getHeaderHeight()));
				layoutParams.setFontSize(layout.get("font.size", Integer.class,
						layoutParams.getFontSize()));
				layoutParams.setHeaderFontSize(layout.get("header.font.size",
						Integer.class, layoutParams.getHeaderFontSize()));
				layoutParams.setHeaderFontName(layout.get("header.font.name",
						String.class, layoutParams.getHeaderFontName()));
				layoutParams.setFontName(layout.get("font.name", String.class,
						layoutParams.getFontName()));
				layoutParams.setConvertToPNG(layout.get("png.output",
						Boolean.class, layoutParams.isConvertToPNG()));
				layoutParams.setConvertToSVG(layout.get("svg.output",
						Boolean.class, layoutParams.isConvertToSVG()));
				layoutParams.setConvertToEPS(layout.get("eps.output",
						Boolean.class, layoutParams.isConvertToEPS()));
			}
			// Load codes
			for (Section section : wini.values()) {
				if (section.getName().startsWith("code.")) {
					String codeName = section.getName().substring(5);
					Long key = section.get("key", Long.class);
					if (key == null)
						throw new IllegalArgumentException(
								"No key specified for code '" + codeName + "'!");
					String title = section.get("title", "");
					Integer layers = section.get("layers", Integer.class);
					if (layers == null)
						throw new IllegalArgumentException(
								"No number of layers specified for code '"
										+ codeName + "'!");
					Integer bands = section.get("bands", Integer.class);
					if (bands == null)
						throw new IllegalArgumentException(
								"No number of bands specified for code '"
										+ codeName + "'!");
					List<String> titles = new ArrayList<String>();
					for (int layer = 0; layer < layers; layer++) {
						String layerTitle = section.get(
								String.format("title.%d", layer + 1),
								String.class, title);
						titles.add(layerTitle);
					}
					codes.put(codeName, new WeavedCodeSequence(key,
							layoutParams.getColumns(), bands, layers, titles));
				}
			}
			// Load texts
			for (Section section : wini.values()) {
				if (section.getName().startsWith("text.")) {
					String textName = section.getName().substring(5);
					String codeName = section.get("code");
					CodeSequence code = codes.get(codeName);
					if (code == null)
						throw new IllegalArgumentException("Unknown code '"
								+ codeName + "' in text '" + textName + "'!");
					String title = section.get("title", "");
					List<String> txtlist = new ArrayList<String>();
					String lasttxtstr = null;
					for (int i = 0; i < code.getLayers(); i++) {
						String txtstr = section.get("text." + (i + 1));
						if (txtstr == null) {
							if (lasttxtstr == null)
								throw new IllegalArgumentException(
										"No text defined for '" + textName
												+ "'!");
							txtstr = lasttxtstr;
						}
						txtlist.add(txtstr);
						lasttxtstr = txtstr;
					}
					texts.put(textName, new TextToEncode(title, txtlist, code));
				}
			}
		}

		System.out.println("Loaded " + codes.size() + " codes and "
				+ texts.size() + " texts.");

		for (String codeName : codes.keySet()) {
			System.out.println("Rendering code '" + codeName + "'...");
			CodeSequence code = codes.get(codeName);
			for (int layer = 0; layer < code.getLayers(); layer++) {
				DecodeGridLayer decodeLayer = new DecodeGridLayer(layoutParams,
						code, layer);
				SimpleSVGWriter writer = new SimpleSVGWriter(decodeLayer,
						codeName + "_" + (layer + 1),
						layoutParams.isConvertToPNG(),
						layoutParams.isConvertToSVG(),
						layoutParams.isConvertToEPS());
				writer.render();
			}
			DebugGridLayer debugLayer = new DebugGridLayer(layoutParams, code);
			SimpleSVGWriter writer3 = new SimpleSVGWriter(debugLayer, codeName,
					layoutParams.isConvertToPNG(),
					layoutParams.isConvertToSVG(),
					layoutParams.isConvertToEPS());
			writer3.render();
		}

		for (String textName : texts.keySet()) {
			System.out.println("Rendering text '" + textName + "'...");
			TextToEncode text = texts.get(textName);
			EncodedTextLayer textLayer = new EncodedTextLayer(layoutParams,
					text);
			SimpleSVGWriter writer2 = new SimpleSVGWriter(textLayer, textName,
					layoutParams.isConvertToPNG(),
					layoutParams.isConvertToSVG(),
					layoutParams.isConvertToEPS());
			writer2.render();
		}
		System.out.println("OK!");
	}

	private static String getVersion() {
		Package thePackage = Main.class.getPackage();
		String implementationVersion = thePackage.getImplementationVersion();
		if (implementationVersion == null)
			return "(unknown)";
		return implementationVersion;
	}
}
