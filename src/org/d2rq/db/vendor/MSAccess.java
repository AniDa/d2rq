package org.d2rq.db.vendor;


import java.sql.Types;
import java.util.regex.Pattern;
import org.d2rq.db.schema.Identifier;
import org.d2rq.db.schema.Identifier.IdentifierParseException;
import org.d2rq.db.types.DataType;
import org.d2rq.db.types.SQLCharacterString;



/**
 * This syntax class implements SQL syntax for MS Access.
 * 
 * @author Anna Dabrowska (anna.dabrowska@deri.org)
 */

public class MSAccess extends SQLServer {

	public MSAccess() {
		super();
	}
	
	@Override
	public DataType getDataType(int jdbcType, String name, int size) {
		if (jdbcType == Types.VARCHAR) {
			return new SQLCharacterString(name, true);
		}
		return super.getDataType(jdbcType, name, size);
	}

	@Override
	public String toString(Identifier identifier) {
		return identifier.isDelimited() 
				? squareBracketEscaper.quote(identifier.getName()) 
				: identifier.getName();
	}
	private final static Quoter squareBracketEscaper = 
		new PatternDoublingQuoterforMSAccess(Pattern.compile("([\\\\\"`])"), "[", "]");
	
	
	/**
	 * Implements the special rules according to http://msdn.microsoft.com/en-us/library/ms175874.aspx
	 */
	@Override
	public Identifier[] parseIdentifiers(String s, int minParts, int maxParts)
			throws IdentifierParseException {
		IdentifierParser parser = new IdentifierParser(s, minParts, maxParts) {
			@Override
			protected boolean isOpeningQuoteChar(char c) {
				return c == '[';
			}
			@Override
			protected boolean isClosingQuoteChar(char c) {
				return c == ']';
			}
			@Override
			protected boolean isIdentifierStartChar(char c) {
				return super.isIdentifierStartChar(c) || c == '_';
			}
			@Override
			protected boolean isIdentifierBodyChar(char c) {
				return super.isIdentifierBodyChar(c) || c == '$' || c == '@' || c == '#';
			}
		};
		if (parser.error() == null) {
			return parser.result();
		} else {
			throw new IdentifierParseException(parser.error(), parser.message());
		}
	}
	
}
