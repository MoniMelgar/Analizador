import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonLexer {
    private static final String SPECIAL_TOKENS_REGEX = "[()\\[\\]{}.,;]";
    private static final String COMMENTS_REGEX = "(#.*?$|'''([\\s\\S]*?)''')";
    private static final String KEYWORDS_REGEX = "\\b(if|else|for|while|def|print)\\b";
    private static final String IDENTIFIER_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";
    private static final String OPERATORS_REGEX = "[+\\-*/]";
    private static final String NUMBER_REGEX = "\\d+(\\.\\d+)?";
    private static final String STRING_REGEX = "\"([^\"]*)\"";
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String NEWLINE_REGEX = "\\\\n";  // Escaped regex for '\n'
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el código a analizar: ");
        String code = scanner.nextLine();
        
        List<Token> tokens = tokenize(code);
        
        for (Token token : tokens) {
            System.out.println(token);
        }
        
        scanner.close();
    }

    public static List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();
        int line = 1;
        int column = 1;
        
        String regex = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s", SPECIAL_TOKENS_REGEX, COMMENTS_REGEX, KEYWORDS_REGEX, IDENTIFIER_REGEX, OPERATORS_REGEX, NUMBER_REGEX, STRING_REGEX, WHITESPACE_REGEX, NEWLINE_REGEX);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        
        while (matcher.find()) {
            String match = matcher.group();
            TokenType type;
            
            if (match.matches(SPECIAL_TOKENS_REGEX)) {
                type = TokenType.SPECIAL_TOKEN;
            } else if (match.matches(COMMENTS_REGEX)) {
                type = TokenType.COMMENT;
            } else if (match.matches(KEYWORDS_REGEX)) {
                type = TokenType.KEYWORD;
            } else if (match.matches(IDENTIFIER_REGEX)) {
                type = TokenType.IDENTIFIER;
            } else if (match.matches(OPERATORS_REGEX)) {
                type = TokenType.OPERATOR;
            } else if (match.matches(NUMBER_REGEX)) {
                type = TokenType.NUMBER;
            } else if (match.matches(STRING_REGEX)) {
                type = TokenType.STRING;
            } else if (match.matches(WHITESPACE_REGEX)) {
                type = TokenType.WHITESPACE;
            } else {
                type = TokenType.NEWLINE;
                line++;  // Increment line counter
                column = 1;  // Reset column counter
                continue;  // Skip adding newline token to tokens list
            }
            
            Token token = new Token(type, match, line, column);
            tokens.add(token);
            
            int tokenLength = match.length();
            int newlinesCount = countNewlines(match);
            line += newlinesCount;
            
            if (newlinesCount > 0) {
                column = tokenLength - (matcher.start() - matcher.end());
            } else {
                column += tokenLength;
            }
        }
        
        return tokens;
    }
    
    private static int countNewlines(String text) {
        int count = 0;
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            if (text.charAt(i) == '\n') {
                count++;
            }
        }
        
        return count;
    }
}

class Token {
    private TokenType type;
    private String value;
    private int line;
    private int column;
    
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return String.format("(%s, %s, line: %d, column: %d)", type, value, line, column);
    }
}

enum TokenType {
    SPECIAL_TOKEN,
    COMMENT,
    KEYWORD,
    IDENTIFIER,
    OPERATOR,
    NUMBER,
    STRING,
    WHITESPACE,
    NEWLINE
}
