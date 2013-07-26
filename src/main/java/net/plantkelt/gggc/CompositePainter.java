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

import java.awt.Graphics2D;

public class CompositePainter implements Paintable {

	private Paintable[] paintables;

	public CompositePainter(Paintable... paintables) {
		this.paintables = paintables;
	}

	public void paint(Graphics2D graphics) {
		for (Paintable paintable : paintables) {
			paintable.paint(graphics);
		}
	}

	public int getWidth() {
		int width = 0;
		for (Paintable paintable : paintables) {
			int w = paintable.getWidth();
			if (w > width)
				width = w;
		}
		return width;
	}

	public int getHeight() {
		int height = 0;
		for (Paintable paintable : paintables) {
			int h = paintable.getHeight();
			if (h > height)
				height = h;
		}
		return height;
	}

}
