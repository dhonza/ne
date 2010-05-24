// $ANTLR 3.2 Sep 23, 2009 12:02:23 /home/drchaj1/java/ne/src/common/pmatrix/Property.g 2010-05-24 19:22:49

package common.pmatrix;

import org.antlr.runtime.*;

public class PropertyParser extends Parser {
    public static final String[] tokenNames = new String[]{
            "<invalid>", "<EOR>", "<DOWN>", "<UP>", "INT", "DBL", "BOOL", "STRING", "WS", "'('", "';'", "')'"
    };
    public static final int WS = 8;
    public static final int DBL = 5;
    public static final int T__11 = 11;
    public static final int T__10 = 10;
    public static final int BOOL = 6;
    public static final int INT = 4;
    public static final int EOF = -1;
    public static final int T__9 = 9;
    public static final int STRING = 7;

    // delegates
    // delegators


    public PropertyParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }

    public PropertyParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);

    }


    public String[] getTokenNames() {
        return PropertyParser.tokenNames;
    }

    public String getGrammarFileName() {
        return "/home/drchaj1/java/ne/src/common/pmatrix/Property.g";
    }


    // NOT GENERATED
    private ParameterMatrixBuilder builder = null;
    private String name = null;

    public void setBuilder(ParameterMatrixBuilder builder) {
        this.builder = builder;
    }

    public void setName(String name) {
        this.name = name;
    }
    // END NOT GENERATED


    // $ANTLR start "expr"
    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:30:1: expr : ( number | range | bool | string );
    public final void expr() throws RecognitionException {
        try {
            // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:31:2: ( number | range | bool | string )
            int alt1 = 4;
            switch (input.LA(1)) {
                case INT:
                case DBL: {
                    alt1 = 1;
                }
                break;
                case 9: {
                    alt1 = 2;
                }
                break;
                case BOOL: {
                    alt1 = 3;
                }
                break;
                case STRING: {
                    alt1 = 4;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 1, 0, input);

                    throw nvae;
            }

            switch (alt1) {
                case 1:
                    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:31:4: number
                {
                    pushFollow(FOLLOW_number_in_expr33);
                    number();

                    state._fsp--;


                }
                break;
                case 2:
                    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:31:13: range
                {
                    pushFollow(FOLLOW_range_in_expr37);
                    range();

                    state._fsp--;


                }
                break;
                case 3:
                    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:31:21: bool
                {
                    pushFollow(FOLLOW_bool_in_expr41);
                    bool();

                    state._fsp--;


                }
                break;
                case 4:
                    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:31:28: string
                {
                    pushFollow(FOLLOW_string_in_expr45);
                    string();

                    state._fsp--;


                }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }
    // $ANTLR end "expr"


    // $ANTLR start "range"
    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:32:1: range : ( '(' from= INT ';' to= INT ';' step= INT ')' | '(' from= DBL ';' to= DBL ';' step= DBL ')' );
    public final void range() throws RecognitionException {
        Token from = null;
        Token to = null;
        Token step = null;

        try {
            // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:33:2: ( '(' from= INT ';' to= INT ';' step= INT ')' | '(' from= DBL ';' to= DBL ';' step= DBL ')' )
            int alt2 = 2;
            int LA2_0 = input.LA(1);

            if ((LA2_0 == 9)) {
                int LA2_1 = input.LA(2);

                if ((LA2_1 == INT)) {
                    alt2 = 1;
                } else if ((LA2_1 == DBL)) {
                    alt2 = 2;
                } else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 2, 1, input);

                    throw nvae;
                }
            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1:
                    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:33:4: '(' from= INT ';' to= INT ';' step= INT ')'
                {
                    match(input, 9, FOLLOW_9_in_range53);
                    from = (Token) match(input, INT, FOLLOW_INT_in_range57);
                    match(input, 10, FOLLOW_10_in_range59);
                    to = (Token) match(input, INT, FOLLOW_INT_in_range63);
                    match(input, 10, FOLLOW_10_in_range65);
                    step = (Token) match(input, INT, FOLLOW_INT_in_range69);
                    match(input, 11, FOLLOW_11_in_range71);
                    builder.addRange(name, Integer.parseInt((from != null ? from.getText() : null)), Integer.parseInt((to != null ? to.getText() : null)), Integer.parseInt((step != null ? step.getText() : null)));

                }
                break;
                case 2:
                    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:34:4: '(' from= DBL ';' to= DBL ';' step= DBL ')'
                {
                    match(input, 9, FOLLOW_9_in_range78);
                    from = (Token) match(input, DBL, FOLLOW_DBL_in_range82);
                    match(input, 10, FOLLOW_10_in_range84);
                    to = (Token) match(input, DBL, FOLLOW_DBL_in_range88);
                    match(input, 10, FOLLOW_10_in_range90);
                    step = (Token) match(input, DBL, FOLLOW_DBL_in_range94);
                    match(input, 11, FOLLOW_11_in_range96);
                    builder.addRange(name, Double.parseDouble((from != null ? from.getText() : null)), Double.parseDouble((to != null ? to.getText() : null)), Double.parseDouble((step != null ? step.getText() : null)));

                }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }
    // $ANTLR end "range"


    // $ANTLR start "number"
    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:35:1: number : ( INT | DBL );
    public final void number() throws RecognitionException {
        Token INT1 = null;
        Token DBL2 = null;

        try {
            // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:36:2: ( INT | DBL )
            int alt3 = 2;
            int LA3_0 = input.LA(1);

            if ((LA3_0 == INT)) {
                alt3 = 1;
            } else if ((LA3_0 == DBL)) {
                alt3 = 2;
            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1:
                    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:36:4: INT
                {
                    INT1 = (Token) match(input, INT, FOLLOW_INT_in_number108);
                    builder.add(new Parameter<Integer>(name, Integer.parseInt((INT1 != null ? INT1.getText() : null))));

                }
                break;
                case 2:
                    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:37:4: DBL
                {
                    DBL2 = (Token) match(input, DBL, FOLLOW_DBL_in_number115);
                    builder.add(new Parameter<Double>(name, Double.parseDouble((DBL2 != null ? DBL2.getText() : null))));

                }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }
    // $ANTLR end "number"


    // $ANTLR start "bool"
    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:38:1: bool : BOOL ;
    public final void bool() throws RecognitionException {
        Token BOOL3 = null;

        try {
            // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:39:2: ( BOOL )
            // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:39:4: BOOL
            {
                BOOL3 = (Token) match(input, BOOL, FOLLOW_BOOL_in_bool126);
                builder.add(new Parameter<Boolean>(name, Boolean.valueOf((BOOL3 != null ? BOOL3.getText() : null))));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }
    // $ANTLR end "bool"


    // $ANTLR start "string"
    // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:40:1: string : STRING ;
    public final void string() throws RecognitionException {
        Token STRING4 = null;

        try {
            // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:41:2: ( STRING )
            // /home/drchaj1/java/ne/src/common/pmatrix/Property.g:41:4: STRING
            {
                STRING4 = (Token) match(input, STRING, FOLLOW_STRING_in_string138);
                String s = (STRING4 != null ? STRING4.getText() : null);
                builder.add(new Parameter<String>(name, s.substring(1, s.length() - 1)));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }
    // $ANTLR end "string"

    // Delegated rules


    public static final BitSet FOLLOW_number_in_expr33 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_expr37 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_in_expr41 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_in_expr45 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_9_in_range53 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_INT_in_range57 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_range59 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_INT_in_range63 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_range65 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_INT_in_range69 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_range71 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_9_in_range78 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DBL_in_range82 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_range84 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DBL_in_range88 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_range90 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DBL_in_range94 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_range96 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_number108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DBL_in_number115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_bool126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_string138 = new BitSet(new long[]{0x0000000000000002L});

}