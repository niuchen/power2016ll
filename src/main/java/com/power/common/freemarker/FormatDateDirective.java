package com.power.common.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.power.common.utils.IntTimeUtil;



import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 日期格式转换
 * int 转换为String
 */
public class FormatDateDirective implements TemplateDirectiveModel {
	public static final String PARAM_S = "s";
	public static final String PATTERN = "p";

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Integer s = DirectiveUtils.getInt(PARAM_S, params);
		String p = DirectiveUtils.getString(PATTERN, params);
		if (s != null) {
			Writer out = env.getOut();
			if (p!= null) {
				out.append(IntTimeUtil.getFormatDateTime(s.intValue(),p));
			} else {
				out.append(IntTimeUtil.getFormatDateTime(s.intValue()));
			}
		}
	}
}
