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

public class Count<T> implements Comparable<Count<T>> {

	private T data;
	private long count = 0;

	public Count(T data) {
		this.data = data;
	}

	void inc(long n) {
		count += n;
	}

	public T getData() {
		return data;
	}

	public long getCount() {
		return count;
	}

	@Override
	public int compareTo(Count<T> o) {
		return Long.compare(o.count, count);
	}

	@Override
	public String toString() {
		return data.toString() + " (" + count + ")";
	}
}
