package view.calculators;

import model.manager.EngineModelInterface.Calculator;
import controller.calculators.CalculatorController;
import utils.CalcException;
/**
 * 
 *  Implementation of the CombinatoricsLogics interface.
 *
 */
public class CombinatoricsLogicsImpl implements CombinatoricsLogics {

    private final CalculatorController controller = Calculator.COMBINATORICS.getController();
    private String opString = "";
    private String opFormat = "";

    @Override
    public String numberAction(final String btnText) {
        this.controller.getManager().memory().read(btnText);
        try {
            return this.getDisplayText();
        } catch (CalcException e1) {
            this.clearStrings();
            this.controller.getManager().memory().clear();
            return "Syntax Error";
        }
    }

    @Override
    public String calculateAction() {
        String adder = "";
        try {
            adder = this.controller.isBinaryOperator(this.opString) ? this.getStream().split(this.opString)[1] : "";
        } catch (ArrayIndexOutOfBoundsException e2) {
            this.clearStrings();
            this.controller.getManager().memory().clear();
        }
        adder += this.opString.isBlank() ? "" : ") =";
        final String result = this.opFormat + adder;
        this.controller.getManager().engine().calculate();
        this.controller.getManager().memory().addResult(this.opString.isBlank() ? "" : result + " " + this.getStream());
        this.clearStrings();
        return result;
    }

    @Override
    public String getStream() {
        return this.controller.getManager().memory().getCurrentState().stream().reduce("", (a, b) -> a + b);
    }

    @Override
    public String backspaceAction() {
        try {
            if (this.controller.getManager().memory().getCurrentState().get(this.controller.getManager().memory().getCurrentState().size() - 1).length() > 1) {
                this.clearStrings();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
        this.controller.getManager().memory().deleteLast();
        try {
            return this.getDisplayText();
        } catch (CalcException e1) {
            this.clearStrings();
            this.controller.getManager().memory().clear();
            return " ";
        }
    }

    @Override
    public String opAction(final String btnName, final String opName) {
        if (this.getStream().isBlank() || !this.opString.isBlank()) {
            return "Syntax Error";
        }
        final String closer = this.controller.isBinaryOperator(opName) ? ", " : "";
        this.opFormat = btnName + "(" + this.getStream() + closer;
        this.controller.getManager().memory().read(opName);
        this.opString = opName;
        return this.opFormat + ")";
    }

    private String getDisplayText() throws CalcException {
        if (!this.opFormat.isBlank()) {
            if (!this.opString.isBlank() && this.controller.isBinaryOperator(this.opString)) {
                try {
                    return this.opFormat + this.getStream().split(this.opString)[1] + ")";
                } catch (ArrayIndexOutOfBoundsException e3) {
                    return this.opFormat;
                }
            } else {
                throw new CalcException("Syntax error");
            }
        } else {
            return this.getStream();
        }
    }

    private void clearStrings() {
        this.opFormat = "";
        this.opString = "";
    }
}
