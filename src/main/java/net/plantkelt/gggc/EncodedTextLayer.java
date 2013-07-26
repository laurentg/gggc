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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Random;

public class EncodedTextLayer implements Paintable {

	private LayoutParameters layoutParams;
	private TextToEncode text;
	private int maxRow = 0;

	public EncodedTextLayer(LayoutParameters layoutParams, TextToEncode text) {
		this.layoutParams = layoutParams;
		this.text = text;
	}

	public void paint(Graphics2D graphics) {
		Random rand = new Random(text.getTitle().hashCode());
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font(layoutParams.getHeaderFontName(), Font.PLAIN,
				layoutParams.getHeaderFontSize()));
		FontMetrics fontMetrics = graphics.getFontMetrics();
		graphics.drawString(text.getTitle(), 0, fontMetrics.getHeight());
		Font gridFont = new Font(layoutParams.getFontName(), Font.BOLD,
				layoutParams.getFontSize());
		graphics.setFont(gridFont);
		fontMetrics = graphics.getFontMetrics();
		CodeSequence code = text.getCode();
		graphics.setStroke(new BasicStroke(layoutParams.getCellHeight() / 10));
		int col = 0, row = 0;
		int rows = code.getRows();
		int cols = code.getCols();
		int layers = code.getLayers();
		int page = 0;
		boolean finished = false;
		boolean layerFinished[] = new boolean[layers];
		int nLttTxt[] = new int[layers];
		int baseIndex[] = new int[layers];
		int maxIndex[] = new int[layers];
		while (!finished) {
			int layer = code.getLayerAt(col, row);
			char chr;
			if (layer == -1) {
				String txt = text.getText(rand.nextInt(layers));
				chr = txt.charAt(rand.nextInt(txt.length()));
			} else {
				int index = code.getIndexAt(col, row) + baseIndex[layer];
				if (index > maxIndex[layer])
					maxIndex[layer] = index;
				String txt = text.getText(layer);
				if (index < txt.length()) {
					chr = txt.charAt(index);
					nLttTxt[layer]++;
					if (nLttTxt[layer] == txt.length()) {
						layerFinished[layer] = true;
					}
				} else if (index == txt.length()) {
					chr = '/';
				} else {
					String txt2 = text.getText(rand.nextInt(layers));
					chr = txt2.charAt(rand.nextInt(txt2.length()));
				}
			}
			int width = fontMetrics.charWidth((int) chr);
			int x = col * layoutParams.getCellWidth()
					+ layoutParams.getCellWidth() / 2;
			int y = (row + page * rows) * layoutParams.getCellHeight()
					+ layoutParams.getHeaderHeight() + (col % 2)
					* layoutParams.getCellHeight() / 2;
			graphics.drawString(
					"" + chr,
					x - width / 2,
					y + layoutParams.getCellHeight()
							- layoutParams.getCellHeight() / 3);
			col++;
			if (col >= cols) {
				col = 0;
				row++;
				if (row >= rows) {
					page++;
					row = 0;
					for (int l = 0; l < layers; l++) {
						baseIndex[l] = maxIndex[l] + 1;
					}
					graphics.setColor(Color.BLUE);
					graphics.drawLine(0, layoutParams.getHeaderHeight() + page
							* rows * layoutParams.getCellHeight(),
							layoutParams.getCellWidth(),
							layoutParams.getHeaderHeight() + page * rows
									* layoutParams.getCellHeight());
					graphics.setColor(Color.BLACK);
				}
			}
			finished = true;
			for (boolean f : layerFinished)
				if (!f) {
					finished = false;
					break;
				}
		}
		maxRow = row + page * rows;
	}

	public int getWidth() {
		return layoutParams.getCellWidth() * layoutParams.getColumns();
	}

	public int getHeight() {
		return layoutParams.getCellHeight() * (maxRow + 2)
				+ layoutParams.getHeaderHeight();
	}
}
