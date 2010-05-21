// $ANTLR 3.1.2 /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g 2009-06-26 19:05:23

package common.pmatrix;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class PropertyLexer extends Lexer {
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

    public PropertyLexer() {;} 
    public PropertyLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public PropertyLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g"; }

    // $ANTLR start "T__9"
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:7:6: ( ',' )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:7:8: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__9"

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:8:7: ( '(' )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:8:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:9:7: ( ';' )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:9:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:10:7: ( ')' )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:10:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:46:6: ( ( '-' | '+' )? ( '0' .. '9' )+ )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:46:10: ( '-' | '+' )? ( '0' .. '9' )+
            {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:46:10: ( '-' | '+' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='+'||LA1_0=='-') ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:46:20: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:46:20: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "DBL"
    public final void mDBL() throws RecognitionException {
        try {
            int _type = DBL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:6: ( ( '-' | '+' )? ( '0' .. '9' )* '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '-' | '+' )? ( '0' .. '9' )+ )? )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:10: ( '-' | '+' )? ( '0' .. '9' )* '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '-' | '+' )? ( '0' .. '9' )+ )?
            {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:10: ( '-' | '+' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='+'||LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:20: ( '0' .. '9' )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:20: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match('.'); 
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:32: ( '0' .. '9' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:32: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);

            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:41: ( ( 'e' | 'E' ) ( '-' | '+' )? ( '0' .. '9' )+ )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='E'||LA8_0=='e') ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:42: ( 'e' | 'E' ) ( '-' | '+' )? ( '0' .. '9' )+
                    {
                    if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:51: ( '-' | '+' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='+'||LA6_0=='-') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:61: ( '0' .. '9' )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:47:61: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DBL"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:48:8: ( '\"' ( . )* '\"' )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:48:12: '\"' ( . )* '\"'
            {
            match('\"'); 
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:48:15: ( . )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0=='\"') ) {
                    alt9=2;
                }
                else if ( ((LA9_0>='\u0000' && LA9_0<='!')||(LA9_0>='#' && LA9_0<='\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:48:15: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:49:6: ( ( ' ' | '\\t' )+ )
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:49:10: ( ' ' | '\\t' )+
            {
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:49:10: ( ' ' | '\\t' )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0=='\t'||LA10_0==' ') ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);

            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "BOOL"
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:49:6: ( 'true' | 'TRUE' | 'false' | 'FALSE' )
            int alt11=4;
            switch ( input.LA(1) ) {
            case 't':
                {
                alt11=1;
                }
                break;
            case 'T':
                {
                alt11=2;
                }
                break;
            case 'f':
                {
                alt11=3;
                }
                break;
            case 'F':
                {
                alt11=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:49:10: 'true'
                    {
                    match("true"); 


                    }
                    break;
                case 2 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:49:17: 'TRUE'
                    {
                    match("TRUE"); 


                    }
                    break;
                case 3 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:49:24: 'false'
                    {
                    match("false"); 


                    }
                    break;
                case 4 :
                    // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:49:32: 'FALSE'
                    {
                    match("FALSE"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOL"

    public void mTokens() throws RecognitionException {
        // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:8: ( T__9 | T__10 | T__11 | T__12 | INT | DBL | STRING | WS | BOOL )
        int alt12=9;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:10: T__9
                {
                mT__9(); 

                }
                break;
            case 2 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:15: T__10
                {
                mT__10(); 

                }
                break;
            case 3 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:21: T__11
                {
                mT__11(); 

                }
                break;
            case 4 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:27: T__12
                {
                mT__12(); 

                }
                break;
            case 5 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:33: INT
                {
                mINT(); 

                }
                break;
            case 6 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:37: DBL
                {
                mDBL(); 

                }
                break;
            case 7 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:41: STRING
                {
                mSTRING(); 

                }
                break;
            case 8 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:48: WS
                {
                mWS(); 

                }
                break;
            case 9 :
                // /home/drchaj1/java/neuro_evolution/src/common/pmatrix/Property.g:1:51: BOOL
                {
                mBOOL(); 

                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\6\uffff\1\13\5\uffff";
    static final String DFA12_eofS =
        "\14\uffff";
    static final String DFA12_minS =
        "\1\11\4\uffff\2\56\5\uffff";
    static final String DFA12_maxS =
        "\1\164\4\uffff\2\71\5\uffff";
    static final String DFA12_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\2\uffff\1\6\1\7\1\10\1\11\1\5";
    static final String DFA12_specialS =
        "\14\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\11\26\uffff\1\11\1\uffff\1\10\5\uffff\1\2\1\4\1\uffff\1\5"+
            "\1\1\1\5\1\7\1\uffff\12\6\1\uffff\1\3\12\uffff\1\12\15\uffff"+
            "\1\12\21\uffff\1\12\15\uffff\1\12",
            "",
            "",
            "",
            "",
            "\1\7\1\uffff\12\6",
            "\1\7\1\uffff\12\6",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__9 | T__10 | T__11 | T__12 | INT | DBL | STRING | WS | BOOL );";
        }
    }
 

}