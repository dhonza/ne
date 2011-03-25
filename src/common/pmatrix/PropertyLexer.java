// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g 2011-03-25 12:54:53

package common.pmatrix;


import org.antlr.runtime.*;

public class PropertyLexer extends Lexer {
    public static final int WS = 9;
    public static final int EXP = 8;
    public static final int DBL = 5;
    public static final int T__12 = 12;
    public static final int T__11 = 11;
    public static final int T__10 = 10;
    public static final int BOOL = 6;
    public static final int INT = 4;
    public static final int EOF = -1;
    public static final int STRING = 7;

    // delegates
    // delegators

    public PropertyLexer() {
        ;
    }

    public PropertyLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }

    public PropertyLexer(CharStream input, RecognizerSharedState state) {
        super(input, state);

    }

    public String getGrammarFileName() {
        return "/Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g";
    }

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:7:7: ( '(' )
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:7:9: '('
            {
                match('(');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:8:7: ( ';' )
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:8:9: ';'
            {
                match(';');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:9:7: ( ')' )
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:9:9: ')'
            {
                match(')');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:44:6: ( ( '-' | '+' )? ( '0' .. '9' )+ )
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:44:10: ( '-' | '+' )? ( '0' .. '9' )+
            {
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:44:10: ( '-' | '+' )?
                int alt1 = 2;
                int LA1_0 = input.LA(1);

                if ((LA1_0 == '+' || LA1_0 == '-')) {
                    alt1 = 1;
                }
                switch (alt1) {
                    case 1:
                        // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:
                    {
                        if (input.LA(1) == '+' || input.LA(1) == '-') {
                            input.consume();

                        } else {
                            MismatchedSetException mse = new MismatchedSetException(null, input);
                            recover(mse);
                            throw mse;
                        }


                    }
                    break;

                }

                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:44:20: ( '0' .. '9' )+
                int cnt2 = 0;
                loop2:
                do {
                    int alt2 = 2;
                    int LA2_0 = input.LA(1);

                    if (((LA2_0 >= '0' && LA2_0 <= '9'))) {
                        alt2 = 1;
                    }


                    switch (alt2) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:44:20: '0' .. '9'
                        {
                            matchRange('0', '9');

                        }
                        break;

                        default:
                            if (cnt2 >= 1) break loop2;
                            EarlyExitException eee =
                                    new EarlyExitException(2, input);
                            throw eee;
                    }
                    cnt2++;
                } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "DBL"
    public final void mDBL() throws RecognitionException {
        try {
            int _type = DBL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXP )? | '.' ( '0' .. '9' )+ ( EXP )? | ( '0' .. '9' )+ EXP )
            int alt9 = 3;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXP )?
                {
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:9: ( '0' .. '9' )+
                    int cnt3 = 0;
                    loop3:
                    do {
                        int alt3 = 2;
                        int LA3_0 = input.LA(1);

                        if (((LA3_0 >= '0' && LA3_0 <= '9'))) {
                            alt3 = 1;
                        }


                        switch (alt3) {
                            case 1:
                                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:10: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt3 >= 1) break loop3;
                                EarlyExitException eee =
                                        new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);

                    match('.');
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:23: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4 = 2;
                        int LA4_0 = input.LA(1);

                        if (((LA4_0 >= '0' && LA4_0 <= '9'))) {
                            alt4 = 1;
                        }


                        switch (alt4) {
                            case 1:
                                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:24: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                break loop4;
                        }
                    } while (true);

                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:34: ( EXP )?
                    int alt5 = 2;
                    int LA5_0 = input.LA(1);

                    if ((LA5_0 == 'E' || LA5_0 == 'e')) {
                        alt5 = 1;
                    }
                    switch (alt5) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:34: EXP
                        {
                            mEXP();

                        }
                        break;

                    }


                }
                break;
                case 2:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:6: '.' ( '0' .. '9' )+ ( EXP )?
                {
                    match('.');
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:9: ( '0' .. '9' )+
                    int cnt6 = 0;
                    loop6:
                    do {
                        int alt6 = 2;
                        int LA6_0 = input.LA(1);

                        if (((LA6_0 >= '0' && LA6_0 <= '9'))) {
                            alt6 = 1;
                        }


                        switch (alt6) {
                            case 1:
                                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:10: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt6 >= 1) break loop6;
                                EarlyExitException eee =
                                        new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:20: ( EXP )?
                    int alt7 = 2;
                    int LA7_0 = input.LA(1);

                    if ((LA7_0 == 'E' || LA7_0 == 'e')) {
                        alt7 = 1;
                    }
                    switch (alt7) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:20: EXP
                        {
                            mEXP();

                        }
                        break;

                    }


                }
                break;
                case 3:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:47:6: ( '0' .. '9' )+ EXP
                {
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:47:6: ( '0' .. '9' )+
                    int cnt8 = 0;
                    loop8:
                    do {
                        int alt8 = 2;
                        int LA8_0 = input.LA(1);

                        if (((LA8_0 >= '0' && LA8_0 <= '9'))) {
                            alt8 = 1;
                        }


                        switch (alt8) {
                            case 1:
                                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:47:7: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt8 >= 1) break loop8;
                                EarlyExitException eee =
                                        new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);

                    mEXP();

                }
                break;

            }
            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "DBL"

    // $ANTLR start "EXP"
    public final void mEXP() throws RecognitionException {
        try {
            int _type = EXP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:48:5: ( ( 'e' | 'E' ) ( '-' | '+' )? ( '0' .. '9' )+ )
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:48:7: ( 'e' | 'E' ) ( '-' | '+' )? ( '0' .. '9' )+
            {
                if (input.LA(1) == 'E' || input.LA(1) == 'e') {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }

                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:48:16: ( '-' | '+' )?
                int alt10 = 2;
                int LA10_0 = input.LA(1);

                if ((LA10_0 == '+' || LA10_0 == '-')) {
                    alt10 = 1;
                }
                switch (alt10) {
                    case 1:
                        // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:
                    {
                        if (input.LA(1) == '+' || input.LA(1) == '-') {
                            input.consume();

                        } else {
                            MismatchedSetException mse = new MismatchedSetException(null, input);
                            recover(mse);
                            throw mse;
                        }


                    }
                    break;

                }

                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:48:26: ( '0' .. '9' )+
                int cnt11 = 0;
                loop11:
                do {
                    int alt11 = 2;
                    int LA11_0 = input.LA(1);

                    if (((LA11_0 >= '0' && LA11_0 <= '9'))) {
                        alt11 = 1;
                    }


                    switch (alt11) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:48:27: '0' .. '9'
                        {
                            matchRange('0', '9');

                        }
                        break;

                        default:
                            if (cnt11 >= 1) break loop11;
                            EarlyExitException eee =
                                    new EarlyExitException(11, input);
                            throw eee;
                    }
                    cnt11++;
                } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "EXP"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:49:8: ( '\"' ( . )* '\"' )
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:49:12: '\"' ( . )* '\"'
            {
                match('\"');
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:49:15: ( . )*
                loop12:
                do {
                    int alt12 = 2;
                    int LA12_0 = input.LA(1);

                    if ((LA12_0 == '\"')) {
                        alt12 = 2;
                    } else if (((LA12_0 >= '\u0000' && LA12_0 <= '!') || (LA12_0 >= '#' && LA12_0 <= '\uFFFF'))) {
                        alt12 = 1;
                    }


                    switch (alt12) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:49:15: .
                        {
                            matchAny();

                        }
                        break;

                        default:
                            break loop12;
                    }
                } while (true);

                match('\"');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:50:6: ( ( ' ' | '\\t' )+ )
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:50:10: ( ' ' | '\\t' )+
            {
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:50:10: ( ' ' | '\\t' )+
                int cnt13 = 0;
                loop13:
                do {
                    int alt13 = 2;
                    int LA13_0 = input.LA(1);

                    if ((LA13_0 == '\t' || LA13_0 == ' ')) {
                        alt13 = 1;
                    }


                    switch (alt13) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:
                        {
                            if (input.LA(1) == '\t' || input.LA(1) == ' ') {
                                input.consume();

                            } else {
                                MismatchedSetException mse = new MismatchedSetException(null, input);
                                recover(mse);
                                throw mse;
                            }


                        }
                        break;

                        default:
                            if (cnt13 >= 1) break loop13;
                            EarlyExitException eee =
                                    new EarlyExitException(13, input);
                            throw eee;
                    }
                    cnt13++;
                } while (true);

                skip();

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "BOOL"
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:50:6: ( 'true' | 'TRUE' | 'false' | 'FALSE' )
            int alt14 = 4;
            switch (input.LA(1)) {
                case 't': {
                    alt14 = 1;
                }
                break;
                case 'T': {
                    alt14 = 2;
                }
                break;
                case 'f': {
                    alt14 = 3;
                }
                break;
                case 'F': {
                    alt14 = 4;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 14, 0, input);

                    throw nvae;
            }

            switch (alt14) {
                case 1:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:50:10: 'true'
                {
                    match("true");


                }
                break;
                case 2:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:50:17: 'TRUE'
                {
                    match("TRUE");


                }
                break;
                case 3:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:50:24: 'false'
                {
                    match("false");


                }
                break;
                case 4:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:50:32: 'FALSE'
                {
                    match("FALSE");


                }
                break;

            }
            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "BOOL"

    public void mTokens() throws RecognitionException {
        // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:8: ( T__10 | T__11 | T__12 | INT | DBL | EXP | STRING | WS | BOOL )
        int alt15 = 9;
        alt15 = dfa15.predict(input);
        switch (alt15) {
            case 1:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:10: T__10
            {
                mT__10();

            }
            break;
            case 2:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:16: T__11
            {
                mT__11();

            }
            break;
            case 3:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:22: T__12
            {
                mT__12();

            }
            break;
            case 4:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:28: INT
            {
                mINT();

            }
            break;
            case 5:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:32: DBL
            {
                mDBL();

            }
            break;
            case 6:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:36: EXP
            {
                mEXP();

            }
            break;
            case 7:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:40: STRING
            {
                mSTRING();

            }
            break;
            case 8:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:47: WS
            {
                mWS();

            }
            break;
            case 9:
                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:1:50: BOOL
            {
                mBOOL();

            }
            break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    protected DFA15 dfa15 = new DFA15(this);
    static final String DFA9_eotS =
            "\5\uffff";
    static final String DFA9_eofS =
            "\5\uffff";
    static final String DFA9_minS =
            "\2\56\3\uffff";
    static final String DFA9_maxS =
            "\1\71\1\145\3\uffff";
    static final String DFA9_acceptS =
            "\2\uffff\1\2\1\1\1\3";
    static final String DFA9_specialS =
            "\5\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }

        public String getDescription() {
            return "45:1: DBL : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXP )? | '.' ( '0' .. '9' )+ ( EXP )? | ( '0' .. '9' )+ EXP );";
        }
    }

    static final String DFA15_eotS =
            "\5\uffff\1\4\5\uffff";
    static final String DFA15_eofS =
            "\13\uffff";
    static final String DFA15_minS =
            "\1\11\4\uffff\1\56\5\uffff";
    static final String DFA15_maxS =
            "\1\164\4\uffff\1\145\5\uffff";
    static final String DFA15_acceptS =
            "\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\5\1\6\1\7\1\10\1\11";
    static final String DFA15_specialS =
            "\13\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\11\26\uffff\1\11\1\uffff\1\10\5\uffff\1\1\1\3\1\uffff\1\4" +
                    "\1\uffff\1\4\1\6\1\uffff\12\5\1\uffff\1\2\11\uffff\1\7\1\12" +
                    "\15\uffff\1\12\20\uffff\1\7\1\12\15\uffff\1\12",
            "",
            "",
            "",
            "",
            "\1\6\1\uffff\12\5\13\uffff\1\6\37\uffff\1\6",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }

        public String getDescription() {
            return "1:1: Tokens : ( T__10 | T__11 | T__12 | INT | DBL | EXP | STRING | WS | BOOL );";
        }
    }


}