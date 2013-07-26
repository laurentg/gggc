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

public class DecodeGridLayer implements Paintable {

	private LayoutParameters layoutParams;
	private CodeSequence code;
	private int layer;

	public DecodeGridLayer(LayoutParameters layoutParams, CodeSequence code,
			int layer) {
		this.layoutParams = layoutParams;
		this.code = code;
		this.layer = layer;
	}

	public void paint(Graphics2D graphics) {
		graphics.setFont(new Font(layoutParams.getHeaderFontName(), Font.PLAIN,
				layoutParams.getHeaderFontSize()));
		FontMetrics fontMetrics = graphics.getFontMetrics();
		int W = layoutParams.getCellWidth();
		int dW = W / 6;
		int H = layoutParams.getCellHeight();
		int HH = layoutParams.getHeaderHeight();
		graphics.drawString(
				code.getTitle(layer).replace("#", "" + (layer + 1)), 0,
				fontMetrics.getHeight());
		graphics.setPaint(Color.BLUE);
		graphics.setStroke(new BasicStroke(H / 10));
		graphics.drawLine(0, HH, W, HH);
		graphics.setPaint(Color.BLACK);
		for (int row = 0; row < code.getRows(); row++) {
			for (int col = 0; col < layoutParams.getColumns(); col++) {
				if (code.getLayerAt(col, row) != layer) {
					// draw an hexagon
					int x0 = col * W;
					int y0 = row * H + HH + (col % 2) * H / 2;
					int xs[] = new int[6];
					int ys[] = new int[6];
					xs[0] = x0 + dW;
					ys[0] = y0;
					xs[1] = x0 + W - dW;
					ys[1] = y0;
					xs[2] = x0 + W + dW;
					ys[2] = y0 + H / 2;
					xs[3] = x0 + W - dW;
					ys[3] = y0 + H;
					xs[4] = x0 + dW;
					ys[4] = y0 + H;
					xs[5] = x0 - dW;
					ys[5] = y0 + H / 2;
					graphics.fillPolygon(xs, ys, 6);
				}
			}
		}
	}

	public int getWidth() {
		return layoutParams.getCellWidth() * code.getCols();
	}

	public int getHeight() {
		return layoutParams.getCellHeight() * code.getRows()
				+ layoutParams.getCellHeight() / 2
				+ layoutParams.getHeaderHeight();
	}
}
