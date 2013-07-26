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

public class TextToEncode {

	private String title;
	private CodeSequence code;
	private List<String> texts;

	public TextToEncode(String title, List<String> texts, CodeSequence code) {
		this.title = title;
		this.texts = texts;
		this.code = code;
	}

	public String getTitle() {
		return title;
	}
	
	public CodeSequence getCode() {
		return code;
	}

	public String getText(int layer) {
		if (layer >= texts.size())
			return texts.get(texts.size() - 1);
		return texts.get(layer);
	}
}
