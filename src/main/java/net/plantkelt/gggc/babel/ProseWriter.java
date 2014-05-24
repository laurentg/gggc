/*
 * GGGC (c) 2011, 2014, Laurent Grégoire <laurent.gregoire@gmail.com>
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

package net.plantkelt.gggc.babel;

import java.util.Random;

public class ProseWriter {

	private int maxLen;
	private Corpus corpusStats;

	private boolean debug = false;

	public ProseWriter(Corpus corpusStats, int maxLen) {
		this.corpusStats = corpusStats;
		this.maxLen = maxLen;
	}

	private final boolean isPattern(char c) {
		return c == '_' || c >= 'A' && c <= 'Z';
	}

	public String write(String pattern) {
		Random random = new Random();
		int i = 0;
		int len = pattern.length();
		StringBuilder sb = new StringBuilder();
		while (i < len) {
			int patLen = 0;
			while (i + patLen < len && patLen < maxLen + 1
					&& isPattern(pattern.charAt(i + patLen))) {
				patLen++;
			}
			if (patLen >= 1) {
				String pat = pattern.substring(i, i + patLen);
				Count<String> chunk = corpusStats.fillChunk(random, pat);
				if (debug)
					System.out.println("FILL [" + pat + "] -> [" + chunk + "]");
				String more = chunk.getData();
				sb.append(more);
				i += more.length();
			}
			// overlap = true;
			boolean insertPunct = true;
			if (i + 1 < len && !isPattern(pattern.charAt(i + 1)))
				insertPunct = false;
			if (i < len && pattern.charAt(i) == '_' && insertPunct) {
				char punct = ' ';
				boolean spaceAfter = false;
				if (i + 1 < len && pattern.charAt(i + 1) == '_') {
					punct = random.nextBoolean() ? '.' : ',';
					spaceAfter = true;
				}
				if (i + 2 < len && pattern.charAt(i + 2) == ' ') {
					punct = ' ';
					spaceAfter = false;
				}
				if (debug)
					System.out.println("PUNCT [" + punct
							+ (spaceAfter ? " " : "") + "]");
				sb.append(punct);
				i++;
				if (spaceAfter) {
					sb.append(' ');
					i++;
				}
			}
			while (i < len && !isPattern(pattern.charAt(i))) {
				char c = pattern.charAt(i);
				if (debug)
					System.out.println("FWD [" + c + "]");
				sb.append(c);
				i++;
				if ((c == '.' || c == ',')
						&& (i < len && pattern.charAt(i) == '_')) {
					if (debug)
						System.out.println("FWD SPC [" + c + "]");
					sb.append(' ');
					i++;
				}
			}
		}
		return sb.toString();
	}

}
