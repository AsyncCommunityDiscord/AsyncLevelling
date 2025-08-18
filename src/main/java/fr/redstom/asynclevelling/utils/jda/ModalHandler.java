package fr.redstom.asynclevelling.utils.jda;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ModalHandler {

    /** The id of the modal to handle */
    String value();
}
