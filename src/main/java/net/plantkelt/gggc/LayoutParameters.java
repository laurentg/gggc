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

public class LayoutParameters {

	private int cellWidth;
	private int cellHeight;
	private int fontSize;
	private int headerHeight;
	private int headerFontSize;
	private int columns;
	private String headerFontName;
	private String fontName;
	private boolean convertToPNG;
	private boolean convertToSVG;
	private boolean convertToEPS;

	public LayoutParameters() {
		cellWidth = 80;
		cellHeight = 100;
		columns = 36;
		headerHeight = 140;
		fontSize = 100;
		headerFontSize = 120;
		fontName = "Monospaced";
		headerFontName = "Serif";
		convertToPNG = false;
		convertToSVG = true;
		convertToEPS = false;
	}
	
	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getHeaderHeight() {
		return headerHeight;
	}

	public void setHeaderHeight(int headerHeight) {
		this.headerHeight = headerHeight;
	}

	public int getHeaderFontSize() {
		return headerFontSize;
	}

	public void setHeaderFontSize(int headerFontSize) {
		this.headerFontSize = headerFontSize;
	}

	public String getHeaderFontName() {
		return headerFontName;
	}

	public void setHeaderFontName(String headerFontName) {
		this.headerFontName = headerFontName;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public boolean isConvertToPNG() {
		return convertToPNG;
	}

	public void setConvertToPNG(boolean convertToPNG) {
		this.convertToPNG = convertToPNG;
	}

	public boolean isConvertToSVG() {
		return convertToSVG;
	}

	public void setConvertToSVG(boolean convertToSVG) {
		this.convertToSVG = convertToSVG;
	}

	public boolean isConvertToEPS() {
		return convertToEPS;
	}

	public void setConvertToEPS(boolean convertToEPS) {
		this.convertToEPS = convertToEPS;
	}
}
