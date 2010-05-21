// $ANTLR 3.1.2 /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g 2009-06-26 19:05:22

package common.pmatrix;

import common.pmatrix.ParameterMatrixBuilder;
import common.pmatrix.Parameter;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class PropertyParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "INT", "DBL", "BOOL", "STRING", "WS", "','", "'('", "';'", "')'"
    };
    public static final int WS=8;
    public static final int DBL=5;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__10=10;
    public static final int BOOL=6;
    public static final int INT=4;
    public static final int EOF=-1;
    public static final int T__9=9;
    public static final int STRING=7;

    // delegates
    // delegators


        public PropertyParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public PropertyParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return PropertyParser.tokenNames; }
    public String getGrammarFileName() { return "/home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g"; }


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
    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:30:1: expr : object ( ',' object )* ;
    public final void expr() throws RecognitionException {
        try {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:31:2: ( object ( ',' object )* )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:31:4: object ( ',' object )*
            {
            pushFollow(FOLLOW_object_in_expr33);
            object();

            state._fsp--;

            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:31:11: ( ',' object )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==9) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:31:12: ',' object
            	    {
            	    match(input,9,FOLLOW_9_in_expr36); 
            	    pushFollow(FOLLOW_object_in_expr39);
            	    object();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "expr"


    // $ANTLR start "object"
    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:32:1: object : ( number | range | bool | string );
    public final void object() throws RecognitionException {
        try {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:33:2: ( number | range | bool | string )
            int alt2=4;
            switch ( input.LA(1) ) {
            case INT:
            case DBL:
                {
                alt2=1;
                }
                break;
            case 10:
                {
                alt2=2;
                }
                break;
            case BOOL:
                {
                alt2=3;
                }
                break;
            case STRING:
                {
                alt2=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:33:4: number
                    {
                    pushFollow(FOLLOW_number_in_object50);
                    number();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:33:13: range
                    {
                    pushFollow(FOLLOW_range_in_object54);
                    range();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:33:21: bool
                    {
                    pushFollow(FOLLOW_bool_in_object58);
                    bool();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:33:28: string
                    {
                    pushFollow(FOLLOW_string_in_object62);
                    string();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "object"


    // $ANTLR start "range"
    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:34:1: range : ( '(' from= INT ';' to= INT ';' step= INT ')' | '(' from= DBL ';' to= DBL ';' step= DBL ')' );
    public final void range() throws RecognitionException {
        Token from=null;
        Token to=null;
        Token step=null;

        try {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:35:2: ( '(' from= INT ';' to= INT ';' step= INT ')' | '(' from= DBL ';' to= DBL ';' step= DBL ')' )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==10) ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==INT) ) {
                    alt3=1;
                }
                else if ( (LA3_1==DBL) ) {
                    alt3=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:35:4: '(' from= INT ';' to= INT ';' step= INT ')'
                    {
                    match(input,10,FOLLOW_10_in_range70); 
                    from=(Token)match(input,INT,FOLLOW_INT_in_range74); 
                    match(input,11,FOLLOW_11_in_range76); 
                    to=(Token)match(input,INT,FOLLOW_INT_in_range80); 
                    match(input,11,FOLLOW_11_in_range82); 
                    step=(Token)match(input,INT,FOLLOW_INT_in_range86); 
                    match(input,12,FOLLOW_12_in_range88); 
                    builder.addRange(name, Integer.parseInt((from!=null?from.getText():null)), Integer.parseInt((to!=null?to.getText():null)), Integer.parseInt((step!=null?step.getText():null)));

                    }
                    break;
                case 2 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:36:4: '(' from= DBL ';' to= DBL ';' step= DBL ')'
                    {
                    match(input,10,FOLLOW_10_in_range95); 
                    from=(Token)match(input,DBL,FOLLOW_DBL_in_range99); 
                    match(input,11,FOLLOW_11_in_range101); 
                    to=(Token)match(input,DBL,FOLLOW_DBL_in_range105); 
                    match(input,11,FOLLOW_11_in_range107); 
                    step=(Token)match(input,DBL,FOLLOW_DBL_in_range111); 
                    match(input,12,FOLLOW_12_in_range113); 
                    builder.addRange(name, Double.parseDouble((from!=null?from.getText():null)), Double.parseDouble((to!=null?to.getText():null)), Double.parseDouble((step!=null?step.getText():null)));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "range"


    // $ANTLR start "number"
    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:37:1: number : ( INT | DBL );
    public final void number() throws RecognitionException {
        Token INT1=null;
        Token DBL2=null;

        try {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:38:2: ( INT | DBL )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==INT) ) {
                alt4=1;
            }
            else if ( (LA4_0==DBL) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:38:4: INT
                    {
                    INT1=(Token)match(input,INT,FOLLOW_INT_in_number125); 
                    builder.add(new Parameter<Integer>(name, Integer.parseInt((INT1!=null?INT1.getText():null))));

                    }
                    break;
                case 2 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:39:4: DBL
                    {
                    DBL2=(Token)match(input,DBL,FOLLOW_DBL_in_number132); 
                    builder.add(new Parameter<Double>(name, Double.parseDouble((DBL2!=null?DBL2.getText():null))));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "number"


    // $ANTLR start "bool"
    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:40:1: bool : BOOL ;
    public final void bool() throws RecognitionException {
        Token BOOL3=null;

        try {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:41:2: ( BOOL )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:41:4: BOOL
            {
            BOOL3=(Token)match(input,BOOL,FOLLOW_BOOL_in_bool143); 
            builder.add(new Parameter<Boolean>(name, Boolean.valueOf((BOOL3!=null?BOOL3.getText():null))));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "bool"


    // $ANTLR start "string"
    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:42:1: string : STRING ;
    public final void string() throws RecognitionException {
        Token STRING4=null;

        try {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:43:2: ( STRING )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:43:4: STRING
            {
            STRING4=(Token)match(input,STRING,FOLLOW_STRING_in_string155); 
            String s = (STRING4!=null?STRING4.getText():null); builder.add(new Parameter<String>(name, s.substring(1, s.length()-1)));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "string"

    // Delegated rules


 

    public static final BitSet FOLLOW_object_in_expr33 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_9_in_expr36 = new BitSet(new long[]{0x00000000000004F0L});
    public static final BitSet FOLLOW_object_in_expr39 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_number_in_object50 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_object54 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_in_object58 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_in_object62 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_10_in_range70 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_INT_in_range74 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_range76 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_INT_in_range80 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_range82 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_INT_in_range86 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_range88 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_10_in_range95 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DBL_in_range99 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_range101 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DBL_in_range105 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_range107 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DBL_in_range111 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_range113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_number125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DBL_in_number132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_bool143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_string155 = new BitSet(new long[]{0x0000000000000002L});

}