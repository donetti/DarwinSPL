/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package eu.hyvar.feature.expression.resource.hyexpression.mopp;

import org.antlr.runtime3_4_0.CommonToken;
import org.antlr.runtime3_4_0.Token;

public class HyexpressionANTLRTextToken extends eu.hyvar.feature.expression.resource.hyexpression.mopp.HyexpressionTextToken {
	
	private final static eu.hyvar.feature.expression.resource.hyexpression.IHyexpressionMetaInformation metaInformation = new eu.hyvar.feature.expression.resource.hyexpression.mopp.HyexpressionMetaInformation();
	
	public HyexpressionANTLRTextToken(Token antlrToken) {
		super(getTokenName(metaInformation.getTokenNames(), antlrToken.getType()), antlrToken.getText(), ((CommonToken) antlrToken).getStartIndex(), ((CommonToken) antlrToken).getStopIndex() - ((CommonToken) antlrToken).getStartIndex() + 1, antlrToken.getLine(), antlrToken.getCharPositionInLine(), canBeUsedForSyntaxHighlighting(antlrToken.getType()));
	}
	
	public static String getTokenName(String[] tokenNames, int index) {
		if (tokenNames == null) {
			return null;
		}
		String tokenName = tokenNames[index];
		if (tokenName != null && tokenName.startsWith("'")) {
			tokenName = tokenName.substring(1, tokenName.length() - 1).trim();
		}
		return tokenName;
	}
	
	public static boolean canBeUsedForSyntaxHighlighting(int tokenType) {
		if (tokenType < 0) {
			return false;
		}
		if (tokenType == Token.UP) {
			return false;
		}
		if (tokenType == Token.DOWN) {
			return false;
		}
		if (tokenType == Token.EOR_TOKEN_TYPE) {
			return false;
		}
		if (tokenType == Token.INVALID_TOKEN_TYPE) {
			return false;
		}
		return true;
	}
	
}
