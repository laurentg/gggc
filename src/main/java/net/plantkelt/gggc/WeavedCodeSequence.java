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

import java.util.List;
import java.util.Random;

public class WeavedCodeSequence implements CodeSequence {

	/* Set to true to dump generated grid to stderr. */
	private static final boolean DUMP_GRID = false;

	private long key;
	private int X;
	private int Y;
	private int layers;
	private int bands;
	private int freedom;
	private List<String> titles;
	private int[][] lgrid;
	private int[][] igrid;

	public WeavedCodeSequence(long key, int cols, int bands, int layers,
			List<String> titles) {
		this.key = key;
		this.X = cols;
		this.bands = bands;
		this.freedom = 1;
		this.Y = bands * (layers + 1 + freedom) + 1;
		this.layers = layers;
		this.titles = titles;
		generate();
	}

	private class Cockroach {
		int x;
		int y;
		int ymin;
		int ymax;
		int layer;
		int index;

		@Override
		public String toString() {
			return String.format("Cockroach (x,y)=(%d,%d) layer=%d index=%d",
					x, y, layer, index);
		}
	}

	private void generate() {
		Random rand = new Random(key);
		lgrid = new int[X][Y];
		igrid = new int[X][Y];
		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				lgrid[x][y] = -1;
			}
		}
		/* Create cockroaches. */
		Cockroach crs[] = new Cockroach[layers];
		for (int i = 0; i < crs.length; i++) {
			crs[i] = new Cockroach();
		}
		/* Work band by band. */
		for (int band = 0; band < bands; band++) {
			/* 1. Place cockroaches on the starting line (first column). */
			for (int icr = 0; icr < crs.length; icr++) {
				Cockroach c = crs[icr];
				c.layer = icr;
				c.x = 0;
				c.ymin = band * (layers + 1 + freedom) + 1;
				c.ymax = c.ymin + layers + freedom;
				do {
					c.y = rand.nextInt(layers + freedom) + band
							* (layers + 1 + freedom) + 1;
				} while (lgrid[c.x][c.y] != -1);
				igrid[c.x][c.y] = c.index;
				lgrid[c.x][c.y] = c.layer;
				c.index++;
			}
			/*
			 * 2. Move each cockroach to the right, a bit up or down randomly.
			 * If no place left to move, jump them one column until a free spot
			 * is found. Prevent collisions between cockroaches of the same
			 * layer.
			 */
			boolean finished = false;
			while (!finished) {
				finished = true;
				for (int i = crs.length - 1; i >= 0; i--) {
					Cockroach c = crs[i];
					if (c.x >= X)
						continue;
					finished = false;
					int dx = 0;
					int dy = 0;
					int ntry = 999;
					boolean ok = false;
					while (!ok) {
						if (ntry >= dx + 2) {
							dx++;
							ntry = 0;
						}
						int dymin = dx % 2 == 0 ? -dx / 2 : -(dx + 1) / 2 + c.x
								% 2;
						int dymax = dx % 2 == 0 ? dx / 2 : (dx - 1) / 2 + c.x
								% 2;
						if (c.y + dymin < c.ymin)
							dymin = c.ymin - c.y;
						if (c.y + dymax >= c.ymax)
							dymax = c.ymax - 1 - c.y;
						dy = rand.nextInt(dymax - dymin + 1) + dymin;
						if (c.x + dx >= X)
							break;
						if (c.y + dy >= 0 && c.y + dy < Y
								&& lgrid[c.x + dx][c.y + dy] == -1) {
							ok = true;
						}
						ntry++;
					}
					c.x += dx;
					c.y += dy;
					if (c.x < X) {
						igrid[c.x][c.y] = c.index;
						lgrid[c.x][c.y] = c.layer;
						c.index++;
					}
				}
			}
		}
		if (DUMP_GRID)
			dump();
	}

	private void dump() {
		for (int y = 0; y < Y; y++) {
			for (int xmod = 0; xmod < 2; xmod++) {
				System.err.printf(xmod == 0 ? "" : "   ");
				for (int x = 0; x < X; x++) {
					if (x % 2 == xmod) {
						System.err.printf("%c%02d   ",
								(char) (65 + lgrid[x][y]), igrid[x][y]);
					}
				}
				System.err.printf("\n");
			}
		}
	}

	@Override
	public int getLayerAt(int col, int row) {
		return lgrid[col][row];
	}

	@Override
	public int getIndexAt(int col, int row) {
		return igrid[col][row];
	}

	@Override
	public int getCols() {
		return X;
	}

	@Override
	public int getRows() {
		return Y;
	}

	@Override
	public int getLayers() {
		return layers;
	}

	@Override
	public String getTitle(int layer) {
		return titles.get(layer);
	}

}
