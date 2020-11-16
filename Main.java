import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

//Java code for a simple lexical analyzer.
//Code is modified from code found on github by Mohammed Khan

    public abstract class Main {

        public enum State {
            // non-final states 
            Start, //0 non final start
            Period, //1 . used for floats
            E, //2 used for floats with an exponent
            EPlusMinus, //3 used for floats with an exponent and + or -
            bar,//4 | used for half of logical or
            ampersand,//5 & used for half of logical and
            DblQuote,//6 " used with String
            StringBody,//7 used with String
            SnglQuote,//8 ' used with char
            CharCharacter,//9 used with char
            Dollar,//10 $ used with PerlScalar
            ScalarBody,//11 used with PerlScalar
            AtSign,//12 @ used with PerlArray
            ArrayBody,//13 used with PerlArray
            Percent,//14 % used with PerlHash
            HashBody,//15 used with PerlHash
            Zero,
            X,
            Hex,

            // final states
            Id, //7 used for identifiers
            id_i,//8 used for printing
            id_tru,//9 used for printing
            Semicolon,//10 ; used as special char for Perl Ids
            Assign,//11 = used for assignment
            LBrace,//12 { used for open code block
            RBrace,//13 } used for close code block
            Incr,//14 ++ used for increment
            Decr,//15 -- used for decrement
            Not,//16 ! used as logical not
            Or,//17 || used as logical or
            And,//18 && used as logical and
            Int, // 19 Int used for integer literals
            Float, //20 Float used for floating point literals
            FloatE, //21 FloatE used for floating point liters with exponent
            Add, //22 + used for addition 
            Sub, //23 - used for subtraction
            Mul, //24 * used for multiplication
            Div, //25 / used for division
            Mod, //26 ~ used for modulo operator
            LParen, //27 ( used for open function parameter
            RParen, //28 ) used for close function parameter
            String, //29 "" used to denote String
            PerlScalar, //30 $ used to denote Perl scalars
            PerlArray, //31 @ used to denote Perl arrays
            PerlHash, //32 % used to denote Perl hashes
            Char, //33 ' used to denote characters
            UNDEF //undefined used for errors
        }

        
        public static String t; // holds an extracted token
        public static State state; // the current state of the FA
        private static int a; // the current input character
        private static char c; // used to convert the variable "a" to the char type
        private static BufferedReader input; 
        private static PrintWriter output;  

        private static int getNextChar()
        {
            try {
                return input.read();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        } 
        private static int getChar()
        {
            int i = getNextChar();
            while (Character.isWhitespace((char) i))
                i = getNextChar();
            return i;
        } 

        private static int driver()
        {
            State NEXTSTATE; 

            t = "";
            state = State.Start;

            if (Character.isWhitespace((char) a))
                a = getChar(); 
            if (a == -1) 
                return -1;
            while (a != -1) 
            {
                c = (char) a;
                NEXTSTATE = nextState(state, c);
                if (NEXTSTATE == State.UNDEF) 
                {
                    if (isFinal(state)) 
                     return 1; 
                    else 
                    {
                        t = t + c;
                        a = getNextChar();
                        return 0; 
                    }
                } 
                else 
                {
                    state = NEXTSTATE;
                    t = t + c;
                    a = getNextChar();
                }
            }

            if (isFinal(state)) 
                return 1;
            else
                return 0; 
        }

        private static State nextState(State s, char c)
        {
            switch (state) {
                case Start:
                    
                    if (Character.isLetter(c))
                        return State.Id;
                    else if (Character.isDigit(c))
                        return State.Int;
                    else if (c == '+')
                        return State.Add;
                    else if (c == '-')
                        return State.Sub;
                    else if (c == '*')
                        return State.Mul;
                    else if (c == '/')
                        return State.Div;
                    else if (c == '~')
                        return State.Mod;
                    else if (c == '(')
                        return State.LParen;
                    else if (c == ')')
                        return State.RParen;
                    else if (c == '.')
                        return State.Period;
                    else if (c == '{')
                        return State.LBrace;
                    else if (c == '}')
                        return State.RBrace;
                    else if (c == ';')
                        return State.Semicolon;
                    else if (c == '=')
                        return State.Assign;
                    else if (c == '!')
                        return State.Not;
                    else if (c == '|')
                        return State.bar;
                    else if (c == '&')
                        return State.ampersand;
                    else if (c == '"')
                        return State.DblQuote;
                    else if (c == '$')
                        return State.Dollar;
                    else if (c == '@')
                        return State.AtSign;
                    else if (c == '%')
                        return State.Percent;
                    else if (c == '\'')
                        return State.SnglQuote;
                    /*else if (c == 'x')
                        return State.X;*/
                    else
                        return State.UNDEF;

                case Id:
                    if (Character.isLetterOrDigit(c))
                        return State.Id;
                    else
                        return State.UNDEF;
                case Int:
                    if (Character.isDigit(c))
                        return State.Int;
                    if (c == 'u' || c == 'U' || c == 'l' || c == 'L')
                        return State.Int;
                    else if (c == '.')
                        return State.X;
                    if (c == 'x')
                        return State.Hex;
                    else
                        return State.UNDEF;

                case X:
                    if (c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F')
                        return State.Hex;
                    if (Character.isDigit(c))
                        return State.Hex;
                    else
                        return State.UNDEF;
                case Hex:
                    if (c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F')
                        return State.Hex;
                    if (Character.isDigit(c))
                        return State.Hex;
                    else
                        return State.UNDEF;
                case Period:
                    if (Character.isDigit(c))
                        return State.Float;
                    else
                        return State.UNDEF;
                case Float:
                    if (Character.isDigit(c))
                        return State.Float;
                    else if (c == 'e' || c == 'E')
                        return State.E;
                    if (c == '.')
                        return State.Float;
                    else
                        return State.UNDEF;
                case E:
                    if (Character.isDigit(c))
                        return State.FloatE;
                    else if (c == '+' || c == '-')
                        return State.EPlusMinus;
                    else
                        return State.UNDEF;
                case EPlusMinus:
                    if (Character.isDigit(c))
                        return State.FloatE;
                    else
                        return State.UNDEF;
                case FloatE:
                    if (Character.isDigit(c))
                        return State.FloatE;
                    else
                        return State.UNDEF;
                case Add:
                    if(c == '+')
                        return State.Incr;
                    else
                        return State.UNDEF;
                case Sub:
                    if(c == '-')
                        return State.Decr;
                    else if (Character.isDigit(c))
                        return State.Float;
                    else
                        return State.UNDEF;
                case bar:
                    if(c == '|')
                        return State.Or;
                    else
                        return State.UNDEF;
                case ampersand:
                    if (c == '&')
                        return State.And;
                    else
                        return State.UNDEF;
                case DblQuote:
                    if (Character.isLetterOrDigit(c))
                        return State.StringBody;   
                    if (c == '_' || c == '!' || c == '@' || c == '#' || c == '%' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c =='=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == ';' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~'|| c =='$')
                        return State.StringBody;
                    else
                        return State.UNDEF;
                case StringBody:
                    if (Character.isLetterOrDigit(c))
                        return State.StringBody;
                    if (c == '_' || c == '!' || c == '@' || c == '#' || c == '%' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c == '=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == ';' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~' || c =='$')
                        return State.StringBody;
                    else if (c == '"')
                      return State.String;   
                    else
                      return State.UNDEF;
                case String:
                    if (c == '"')
                      return State.String;   
                    else
                      return State.UNDEF;
                case Dollar:
                    if(Character.isLetterOrDigit(c))
                      return State.ScalarBody;
                    if (c == '_' || c == '!' || c == '#' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c == '=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~')
                        return State.ScalarBody;
                    else
                      return State.UNDEF;
                case ScalarBody:
                    if(Character.isLetterOrDigit(c))
                      return State.ScalarBody;
                    if (c == '_' || c == '!' || c == '#' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c == '=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~')
                        return State.ScalarBody;
                    else if(c == ';')
                      return State.PerlScalar;
                    else
                      return State.UNDEF;
                case PerlScalar:
                    if(c == ';')
                      return State.PerlScalar;
                    else
                      return State.UNDEF;
                case AtSign:
                    if(Character.isLetterOrDigit(c))
                      return State.ArrayBody;
                    if (c == '_' || c == '!' || c == '#' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c == '=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~')
                       return State.ArrayBody;
                    else
                      return State.UNDEF;
                case ArrayBody:
                    if(Character.isLetterOrDigit(c))
                      return State.ArrayBody;
                    if (c == '_' || c == '!' || c == '#' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c == '=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~')
                       return State.ArrayBody;
                    else if(c == ';')
                      return State.PerlArray;
                    else
                      return State.UNDEF;
                case PerlArray:
                    if(c == ';')
                      return State.PerlArray;
                    else
                      return State.UNDEF;
                case Percent:
                    if(Character.isLetterOrDigit(c))
                      return State.HashBody;
                    if (c == '_' || c == '!' || c == '#' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c == '=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~')
                       return State.HashBody;
                    else
                      return State.UNDEF;
                case HashBody:
                    if(Character.isLetterOrDigit(c))
                      return State.HashBody;
                    if (c == '_' || c == '!' || c == '#' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c == '=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~')
                       return State.HashBody;
                    else if(c == ';')
                      return State.PerlHash;
                    else
                      return State.UNDEF;
                case PerlHash:
                    if(c == ';')
                      return State.PerlHash;
                    else
                      return State.UNDEF;
                case SnglQuote:
                    if(Character.isLetterOrDigit(c))
                        return State.CharCharacter;
                    else if (c == '_' || c == '!' || c == '@' || c == '#' || c == '%' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c =='=' || c == '+' || c == '[' || c == ']' || c == '{' || c == '}' || c == '|' || c == ' ' || c == ':' || c == ';' || c == '\'' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?' || c == '`' || c == '~'|| c =='$')
                        return State.CharCharacter;
                    else
                        return State.UNDEF;
                case CharCharacter:
                    if (c == '\'')
                        return State.Char;
                    else
                        return State.UNDEF;
                case Char:
                    if (c == '\'')
                        return State.Char;
                    else
                        return State.UNDEF;
                /*case Hex:
                    if (c == 'x' || c == 'X')
                        return State.Hex;
                    if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
                        return State.Hex;
                    if (c == 'u' || c == 'U' || c == 'l' || c == 'L')
                        return State.Hex;*/
                default:
                    return State.UNDEF;
            }
        } 

        private static boolean isFinal(State passState) {
        
            return (passState.compareTo(State.Id) >= 0);
        }

        public static void displayln(String toDisplay) {
            output.println(toDisplay);
        }

        public static void startLexical(String inputFile, String outputFile)
        {
            try {
                input = new BufferedReader(new FileReader(inputFile));
                output = new PrintWriter(new FileOutputStream(outputFile));
                a = input.read();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 

        public static void closeIO() {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 

        public static void main(String[] args) {
            {
                int i;
                startLexical("test.txt", "output.txt");

                while (a != -1) 
                {
                    i = driver(); 
                    if (i == 1){
                        if(state.compareTo(state.id_i)>=0 && state.compareTo(state.id_tru)<=0){
                            displayln(t+"	: "+ state.Id);
                        }else{
                            displayln(t + "   : " + state.toString());
                        }
                    }
                    else if (i == 0)
                        displayln(t + "  : Lexical Error, invalid Token");
                        //break; //remove comment to make program stop when it encounters an error
                }
                closeIO();
            }
        }
    }
