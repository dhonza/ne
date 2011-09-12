// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g 2011-09-11 23:28:56

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
            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:5: ( ( '-' | '+' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXP )? | ( '-' | '+' )? '.' ( '0' .. '9' )+ ( EXP )? | ( '-' | '+' )? ( '0' .. '9' )+ EXP )
            int alt12 = 3;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:9: ( '-' | '+' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXP )?
                {
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:9: ( '-' | '+' )?
                    int alt3 = 2;
                    int LA3_0 = input.LA(1);

                    if ((LA3_0 == '+' || LA3_0 == '-')) {
                        alt3 = 1;
                    }
                    switch (alt3) {
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

                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:19: ( '0' .. '9' )+
                    int cnt4 = 0;
                    loop4:
                    do {
                        int alt4 = 2;
                        int LA4_0 = input.LA(1);

                        if (((LA4_0 >= '0' && LA4_0 <= '9'))) {
                            alt4 = 1;
                        }


                        switch (alt4) {
                            case 1:
                                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:20: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt4 >= 1) break loop4;
                                EarlyExitException eee =
                                        new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);

                    match('.');
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:33: ( '0' .. '9' )*
                    loop5:
                    do {
                        int alt5 = 2;
                        int LA5_0 = input.LA(1);

                        if (((LA5_0 >= '0' && LA5_0 <= '9'))) {
                            alt5 = 1;
                        }


                        switch (alt5) {
                            case 1:
                                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:34: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                break loop5;
                        }
                    } while (true);

                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:44: ( EXP )?
                    int alt6 = 2;
                    int LA6_0 = input.LA(1);

                    if ((LA6_0 == 'E' || LA6_0 == 'e')) {
                        alt6 = 1;
                    }
                    switch (alt6) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:45:44: EXP
                        {
                            mEXP();

                        }
                        break;

                    }


                }
                break;
                case 2:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:6: ( '-' | '+' )? '.' ( '0' .. '9' )+ ( EXP )?
                {
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:6: ( '-' | '+' )?
                    int alt7 = 2;
                    int LA7_0 = input.LA(1);

                    if ((LA7_0 == '+' || LA7_0 == '-')) {
                        alt7 = 1;
                    }
                    switch (alt7) {
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

                    match('.');
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:19: ( '0' .. '9' )+
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
                                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:20: '0' .. '9'
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

                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:30: ( EXP )?
                    int alt9 = 2;
                    int LA9_0 = input.LA(1);

                    if ((LA9_0 == 'E' || LA9_0 == 'e')) {
                        alt9 = 1;
                    }
                    switch (alt9) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:46:30: EXP
                        {
                            mEXP();

                        }
                        break;

                    }


                }
                break;
                case 3:
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:47:6: ( '-' | '+' )? ( '0' .. '9' )+ EXP
                {
                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:47:6: ( '-' | '+' )?
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

                    // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:47:16: ( '0' .. '9' )+
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
                                // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:47:17: '0' .. '9'
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
                int alt13 = 2;
                int LA13_0 = input.LA(1);

                if ((LA13_0 == '+' || LA13_0 == '-')) {
                    alt13 = 1;
                }
                switch (alt13) {
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
                int cnt14 = 0;
                loop14:
                do {
                    int alt14 = 2;
                    int LA14_0 = input.LA(1);

                    if (((LA14_0 >= '0' && LA14_0 <= '9'))) {
                        alt14 = 1;
                    }


                    switch (alt14) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:48:27: '0' .. '9'
                        {
                            matchRange('0', '9');

                        }
                        break;

                        default:
                            if (cnt14 >= 1) break loop14;
                            EarlyExitException eee =
                                    new EarlyExitException(14, input);
                            throw eee;
                    }
                    cnt14++;
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
                loop15:
                do {
                    int alt15 = 2;
                    int LA15_0 = input.LA(1);

                    if ((LA15_0 == '\"')) {
                        alt15 = 2;
                    } else if (((LA15_0 >= '\u0000' && LA15_0 <= '!') || (LA15_0 >= '#' && LA15_0 <= '\uFFFF'))) {
                        alt15 = 1;
                    }


                    switch (alt15) {
                        case 1:
                            // /Users/drchaj1/java/ne/ne/src/common/pmatrix/Property.g:49:15: .
                        {
                            matchAny();

                        }
                        break;

                        default:
                            break loop15;
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
                int cnt16 = 0;
                loop16:
                do {
                    int alt16 = 2;
                    int LA16_0 = input.LA(1);

                    if ((LA16_0 == '\t' || LA16_0 == ' ')) {
                        alt16 = 1;
                    }


                    switch (alt16) {
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
                            if (cnt16 >= 1) break loop16;
                            EarlyExitException eee =
                                    new EarlyExitException(16, input);
                            throw eee;
                    }
                    cnt16++;
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
            int alt17 = 4;
            switch (input.LA(1)) {
                case 't': {
                    alt17 = 1;
                }
                break;
                case 'T': {
                    alt17 = 2;
                }
                break;
                case 'f': {
                    alt17 = 3;
                }
                break;
                case 'F': {
                    alt17 = 4;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 17, 0, input);

                    throw nvae;
            }

            switch (alt17) {
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
        int alt18 = 9;
        alt18 = dfa18.predict(input);
        switch (alt18) {
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


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA12_eotS =
            "\6\uffff";
    static final String DFA12_eofS =
            "\6\uffff";
    static final String DFA12_minS =
            "\1\53\2\56\3\uffff";
    static final String DFA12_maxS =
            "\2\71\1\145\3\uffff";
    static final String DFA12_acceptS =
            "\3\uffff\1\2\1\3\1\1";
    static final String DFA12_specialS =
            "\6\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\1\1\uffff\1\1\1\3\1\uffff\12\2",
            "\1\3\1\uffff\12\2",
            "\1\5\1\uffff\12\2\13\uffff\1\4\37\uffff\1\4",
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
        for (int i = 0; i < numStates; i++) {
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
            return "45:1: DBL : ( ( '-' | '+' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXP )? | ( '-' | '+' )? '.' ( '0' .. '9' )+ ( EXP )? | ( '-' | '+' )? ( '0' .. '9' )+ EXP );";
        }
    }

    static final String DFA18_eotS =
            "\5\uffff\1\13\6\uffff";
    static final String DFA18_eofS =
            "\14\uffff";
    static final String DFA18_minS =
            "\1\11\3\uffff\2\56\6\uffff";
    static final String DFA18_maxS =
            "\1\164\3\uffff\1\71\1\145\6\uffff";
    static final String DFA18_acceptS =
            "\1\uffff\1\1\1\2\1\3\2\uffff\1\5\1\6\1\7\1\10\1\11\1\4";
    static final String DFA18_specialS =
            "\14\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\11\26\uffff\1\11\1\uffff\1\10\5\uffff\1\1\1\3\1\uffff\1\4" +
                    "\1\uffff\1\4\1\6\1\uffff\12\5\1\uffff\1\2\11\uffff\1\7\1\12" +
                    "\15\uffff\1\12\20\uffff\1\7\1\12\15\uffff\1\12",
            "",
            "",
            "",
            "\1\6\1\uffff\12\5",
            "\1\6\1\uffff\12\5\13\uffff\1\6\37\uffff\1\6",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }

        public String getDescription() {
            return "1:1: Tokens : ( T__10 | T__11 | T__12 | INT | DBL | EXP | STRING | WS | BOOL );";
        }
    }


}