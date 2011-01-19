package gov.nih.nci.cabig.ccts.util.el;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.Expression;
import java.io.StringReader;

import org.apache.commons.el.BinaryOperatorExpression;
import org.apache.commons.el.BooleanLiteral;
import org.apache.commons.el.ExpressionString;
import org.apache.commons.el.parser.ELParser;
import org.apache.commons.el.parser.ParseException;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.Expression;
import java.io.StringReader;

public class EL {

    private MockVariableResolver resolver;
    private MockFunctionMapper mapper;

    public String evaluate(String input) {
        try {
            StringReader rdr = new StringReader(input);
            ELParser parser = new ELParser(rdr);
            Object result = parser.ExpressionString();

            if(result instanceof String) {
                return (String) result;
            } else if(result instanceof Expression) {
                Expression expr = (Expression) result;
                result = expr.evaluate(this.resolver);
                return result == null ? null : result.toString();
            } else if(result instanceof ExpressionString) {
                Expression expr = (Expression) result;
                result = expr.evaluate(this.resolver);
                return result == null ? null : result.toString();
            } else if (result instanceof BinaryOperatorExpression) {
                BinaryOperatorExpression expr = (BinaryOperatorExpression)result;
                result = expr.evaluate(this.resolver, this.mapper, null);
                return result.toString();
            } else if (result instanceof BooleanLiteral) {
                BooleanLiteral expr = (BooleanLiteral)result;
                result = expr.evaluate(this.resolver, this.mapper, null);
                return result.toString();
            } else {
                System.out.println("Incorrect type returned; not String, Expression or ExpressionString");
                return "";
            }
        } catch(ParseException pe) {
            throw new RuntimeException("ParseException thrown: " + pe.getMessage(), pe);
        } catch(ELException ele) {
            throw new RuntimeException("ELException thrown: " + ele.getMessage(), ele);
        }
    }

}
