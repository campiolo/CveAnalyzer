package util;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

/**
 * Implementa um tipo para Field textual que suporte vetores de termos
 * @author rodrigo
 */
public class VectorTextField extends Field {
    
        public static final FieldType vecTextField = new FieldType();

        static {
            vecTextField.setStored(true);
            vecTextField.setStoreTermVectors(true);
            vecTextField.setIndexed(true);
            vecTextField.setTokenized(true);
        }
    
    public VectorTextField(String name, String value) {
        super(name, value, vecTextField);
    }
}
