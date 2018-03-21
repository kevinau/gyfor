package org.plcore.template;

import java.net.URL;
import java.util.List;

public interface IDefaultTemplateLoader {

  public URL findTemplate(String templateName, List<String> tried);

}
