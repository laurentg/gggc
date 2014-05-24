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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class CorpusReader {

	private String content;
	private List<String> chunks;

	public CorpusReader(String file, Charset charset) throws IOException {
		// Brute force: load everything
		System.out.println("Reading " + file);
		byte[] encoded = Files.readAllBytes(Paths.get(file));
		System.out.println("Read " + encoded.length + " bytes");
		System.out.println("Converting and filtering...");
		content = new String(encoded, charset);
		// Remove accents
		content = Normalizer.normalize(content, Normalizer.Form.NFD);
		content = content.replaceAll("\\p{M}", "");
		// Remove non alpha
		content = content.replaceAll("[^A-Za-z]", " ");
		// Replace double-space by one space
		content = content.replaceAll("\\s+", " ");
		// Make all uppercase
		content = content.toUpperCase();

		System.out.println("Scanning...");
		StringTokenizer tokenizer = new StringTokenizer(content, " ");

		chunks = new ArrayList<String>(content.length());
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			chunks.add(token);
			i++;
			if (i % 10000 == 0) {
				System.out.print(".");
			}
		}
		System.out.println("OK.");
		System.out.println("Read " + chunks.size() + " tokens.");
	}

	public Collection<String> getChunks() {
		return chunks;
	}
}
