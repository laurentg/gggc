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

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class SimpleSVGWriter {

	private Paintable painted;
	private String baseFilename;
	private boolean convertToPNG;
	private boolean convertToSVG;
	private boolean convertToEPS;

	public SimpleSVGWriter(Paintable painted, String baseFilename,
			boolean convertToPNG, boolean convertToSVG, boolean convertToEPS) {
		this.painted = painted;
		this.baseFilename = baseFilename;
		this.convertToPNG = convertToPNG;
		this.convertToSVG = convertToSVG;
		this.convertToEPS = convertToEPS;
	}

	public void render() throws Exception {
		// Generate SVG from painter
		DOMImplementation domImpl = GenericDOMImplementation
				.getDOMImplementation();
		Document document = domImpl.createDocument(
				"http://www.w3.org/2000/svg", "svg", null);
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		painted.paint(svgGenerator);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		svgGenerator.setSVGCanvasSize(new Dimension(painted.getWidth(), painted
				.getHeight()));
		Writer out = new OutputStreamWriter(baos, "UTF-8");
		svgGenerator.stream(out, true);
		baos.close();
		byte[] svgContent = baos.toByteArray();

		// Save to SVG file, if needed
		if (convertToSVG) {
			FileOutputStream fos = new FileOutputStream(baseFilename + ".svg");
			fos.write(svgContent);
			fos.close();
		}

		// Convert to EPS, if needed
		if (convertToEPS) {
			EPSTranscoder epsTranscoder = new EPSTranscoder();
			TranscoderInput svgInput = new TranscoderInput(
					new ByteArrayInputStream(svgContent));
			OutputStream epsOutputStream = new FileOutputStream(baseFilename
					+ ".eps");
			TranscoderOutput epsOutput = new TranscoderOutput(epsOutputStream);
			epsTranscoder.transcode(svgInput, epsOutput);
			epsOutputStream.close();
		}

		// Convert to PNG, if needed
		if (convertToPNG) {
			PNGTranscoder pngTranscoder = new PNGTranscoder();
			TranscoderInput svgInput = new TranscoderInput(
					new ByteArrayInputStream(svgContent));
			OutputStream pngOutputStream = new FileOutputStream(baseFilename
					+ ".png");
			TranscoderOutput pngOutput = new TranscoderOutput(pngOutputStream);
			pngTranscoder.transcode(svgInput, pngOutput);
			pngOutputStream.close();
		}
	}
}
