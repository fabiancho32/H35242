package lab2;

/* Generated By:JavaCC: Do not edit this line. compConstants.java */

/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface compConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int LPAREN = 5;
  /** RegularExpression Id. */
  int RPAREN = 6;
  /** RegularExpression Id. */
  int ADD_OP = 7;
  /** RegularExpression Id. */
  int MULT_OP = 8;
  /** RegularExpression Id. */
  int INT = 9;
  /** RegularExpression Id. */
  int BOOL = 10;
  /** RegularExpression Id. */
  int NUMBER = 11;
  /** RegularExpression Id. */
  int ID = 12;
  /** RegularExpression Id. */
  int SEMIC = 13;
  /** RegularExpression Id. */
  int FINARCH = 14;
  /** RegularExpression Id. */
  int ASIGN = 15;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\" \"",
    "\"(\"",
    "\")\"",
    "<ADD_OP>",
    "<MULT_OP>",
    "\"int\"",
    "\"bool\"",
    "<NUMBER>",
    "<ID>",
    "\";\"",
    "\"?\"",
    "\"=\"",
    "\",\"",
  };

}