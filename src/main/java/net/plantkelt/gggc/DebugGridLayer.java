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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class DebugGridLayer implements Paintable {

	private LayoutParameters layoutParams;
	private CodeSequence code;

	public DebugGridLayer(LayoutParameters layoutParams, CodeSequence code) {
		this.layoutParams = layoutParams;
		this.code = code;
	}

	public void paint(Graphics2D graphics) {
		Color COLORS[] = { Color.RED, Color.GREEN, Color.ORANGE, Color.BLUE,
				Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.PINK };
		graphics.setFont(new Font(layoutParams.getFontName(), Font.PLAIN,
				layoutParams.getFontSize() / 2));
		for (int row = 0; row < code.getRows(); row++) {
			for (int col = 0; col < layoutParams.getColumns(); col++) {
				int layer = code.getLayerAt(col, row);
				int index = code.getIndexAt(col, row);
				Color color = Color.BLACK;
				if (layer == -1)
					color = Color.GRAY;
				else if (layer < COLORS.length)
					color = COLORS[layer];
				graphics.setPaint(color);
				int x = col * layoutParams.getCellWidth();
				int y = row * layoutParams.getCellHeight()
						+ layoutParams.getHeaderHeight() + (col % 2)
						* layoutParams.getCellHeight() / 2;
				graphics.fill(new Ellipse2D.Double(x, y, layoutParams
						.getCellWidth(), layoutParams.getCellWidth()));
				if (layer != -1) {
					graphics.setPaint(Color.BLACK);
					graphics.drawString("" + index, x,
							y + layoutParams.getCellHeight() / 2);
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
