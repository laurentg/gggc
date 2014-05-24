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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Corpus {

	private int maxLen;
	private Map<String, Count<String>> chunkCounts = new HashMap<String, Count<String>>();
	private Map<String, List<String>> chunksByPattern = new HashMap<String, List<String>>();

	public Corpus(int maxLen) {
		this.maxLen = maxLen;
	}

	private void addChunk(String chunk, int w) {
		Count<String> count = chunkCounts.get(chunk);
		if (count == null) {
			count = new Count<String>(chunk);
			chunkCounts.put(chunk, count);
			List<String> chunkPatterns = getPatterns(chunk);
			for (String chunkPattern : chunkPatterns) {
				List<String> chunkList = chunksByPattern.get(chunkPattern);
				if (chunkList == null) {
					chunkList = new ArrayList<String>();
					chunksByPattern.put(chunkPattern, chunkList);
				}
				chunkList.add(chunk);
			}
		}
		count.inc(w);
	}

	public void analyze(String chunk) {
		int chunkLen = chunk.length();
		if (chunkLen <= maxLen)
			addChunk(chunk, 1);
	}

	public void analyze(Collection<String> chunks) {
		System.out.println("Analyzing " + chunks.size() + " chunks...");
		int i = 0;
		for (String chunk : chunks) {
			analyze(chunk);
			i++;
			if (i % 10000 == 0) {
				System.out.print(".");
			}
		}
		System.out.println("OK.");
	}

	public Count<String> fillChunk(Random random, String pattern) {
		for (int len = maxLen; len >= 1; len--) {
			if (len > pattern.length())
				continue;
			List<String> chunks = chunksByPattern.get(pattern.subSequence(0,
					len));
			if (chunks != null) {
				List<Count<String>> counts = new ArrayList<Count<String>>();
				for (String chunk : chunks) {
					Count<String> count = chunkCounts.get(chunk);
					counts.add(count);
				}
				Collections.sort(counts);
				Count<String> best = randomPick(random, counts);
				// If the chunk does not fill the pattern,
				// favor one where with "_" after the fill.
				if (best.getData().length() > 1
						&& best.getData().length() < pattern.length()
						&& pattern.charAt(best.getData().length()) != '_') {
					Count<String> best2 = fillChunk(random,
							pattern.substring(0, best.getData().length() - 1));
					if (pattern.charAt(best2.getData().length()) == '_')
						return best2;
				}
				return best;
			}
		}
		return new Count<String>(pattern.substring(0, 1).replace('_', ' '));
	}

	long nextLong(Random rng, long n) {
		long bits, val;
		do {
			bits = (rng.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}

	private Count<String> randomPick(Random rand, List<Count<String>> counts) {
		int rem = 0;
		for (Count<String> count : counts) {
			rem += count.getCount();
		}
		for (Count<String> count : counts) {
			long rnd = nextLong(rand, rem);
			long cnt = count.getCount();
			if (rnd < cnt)
				return count;
			rem -= cnt;
		}
		// Can't happen
		throw new RuntimeException();
	}

	private final List<String> getPatterns(String chunk) {
		List<String> retval = new ArrayList<String>();
		int len = chunk.length();
		// 0 char
		if (len <= 2) {
			retval.add(new String(createEmptyPattern(chunk)));
		}
		// 1 char
		if (len >= 1 && len <= 5) {
			for (int i = 0; i < 3 && i < len; i++) {
				if (i < len - 3)
					continue;
				char[] p = createEmptyPattern(chunk);
				p[i] = chunk.charAt(i);
				retval.add(new String(p));
			}
		}
		if (len >= 3 && len <= 8) {
			// 2 chars
			for (int i = 0; i < 3; i++) {
				for (int j = i + 2; j < i + 4 && j < len; j++) {
					if (j < len - 3)
						continue;
					char[] p = createEmptyPattern(chunk);
					p[i] = chunk.charAt(i);
					p[j] = chunk.charAt(j);
					retval.add(new String(p));
				}
			}
		}
		if (len >= 5 && len <= 11) {
			// 3 chars
			for (int i = 0; i < 3; i++) {
				for (int j = i + 2; j < i + 4 && j < len; j++) {
					for (int k = j + 2; k < j + 4 && k < len; k++) {
						if (k < len - 3)
							continue;
						char[] p = createEmptyPattern(chunk);
						p[i] = chunk.charAt(i);
						p[j] = chunk.charAt(j);
						p[k] = chunk.charAt(k);
						retval.add(new String(p));
					}
				}
			}
		}
		if (len >= 7) {
			// 4 chars
			for (int i = 0; i < 3; i++) {
				for (int j = i + 2; j < i + 4 && j < len; j++) {
					for (int k = j + 2; k < j + 4 && k < len; k++) {
						for (int l = k + 2; l < k + 4 && l < len; l++) {
							char[] p = createEmptyPattern(chunk);
							if (l < len - 3)
								continue;
							p[i] = chunk.charAt(i);
							p[j] = chunk.charAt(j);
							p[k] = chunk.charAt(k);
							p[l] = chunk.charAt(l);
							retval.add(new String(p));
						}
					}
				}
			}
		}
		if (len >= 9) {
			// 5 chars
			for (int i = 0; i < 3; i++) {
				for (int j = i + 2; j < i + 4 && j < len; j++) {
					for (int k = j + 2; k < j + 4 && k < len; k++) {
						for (int l = k + 2; l < k + 4 && l < len; l++) {
							for (int m = l + 2; m < l + 4 && m < len; m++) {
								if (m < len - 3)
									continue;
								char[] p = createEmptyPattern(chunk);
								p[i] = chunk.charAt(i);
								p[j] = chunk.charAt(j);
								p[k] = chunk.charAt(k);
								p[l] = chunk.charAt(l);
								p[m] = chunk.charAt(m);
								retval.add(new String(p));
							}
						}
					}
				}
			}
		}
		if (len >= 11) {
			// 6 chars
			for (int i = 0; i < 3; i++) {
				for (int j = i + 2; j < i + 4 && j < len; j++) {
					for (int k = j + 2; k < j + 4 && k < len; k++) {
						for (int l = k + 2; l < k + 4 && l < len; l++) {
							for (int m = l + 2; m < l + 4 && m < len; m++) {
								for (int n = m + 2; n < m + 4 && n < len; n++) {
									if (n < len - 3)
										continue;
									char[] p = createEmptyPattern(chunk);
									p[i] = chunk.charAt(i);
									p[j] = chunk.charAt(j);
									p[k] = chunk.charAt(k);
									p[l] = chunk.charAt(l);
									p[m] = chunk.charAt(m);
									p[n] = chunk.charAt(n);
									retval.add(new String(p));
								}
							}
						}
					}
				}
			}
		}
		return retval;
	}

	private final char[] createEmptyPattern(String chunk) {
		char[] p = new char[chunk.length()];
		Arrays.fill(p, '_');
		return p;
	}

	public static void main(String[] args) {
		final String WORD = "ABCDEFGHIJKL";
		for (int len = 1; len <= WORD.length(); len++) {
			String word = WORD.substring(0, len);
			System.out.println("*** " + word);
			for (String pattern : new Corpus(12).getPatterns(word))
				System.out.println(pattern);
		}
	}
}
