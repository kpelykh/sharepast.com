package com.sharepast.freemarker;

import com.sharepast.util.spring.SpringConfigurator;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 7/18/11
 * Time: 9:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class NlsMethod
        implements TemplateMethodModelEx {

    @Autowired
    private MessageSource messageSource;

    public Object exec(List arguments)
            throws TemplateModelException {
        if (arguments.size() < 1 || arguments.get(0) == null)
            throw new TemplateModelException(getClass().getName() + ": expected at least one argument, got " + arguments.size());
        Object[] arg = null;
        int sz = arguments.size();
        if (sz > 1) {
            arg = new Object[sz - 1];
            for (int i = 1; i < sz; i++)
                arg[i - 1] = arguments.get(i);
        }

        return messageSource.getMessage(arguments.get(0).toString(), arg, Locale.getDefault());
    }
}

