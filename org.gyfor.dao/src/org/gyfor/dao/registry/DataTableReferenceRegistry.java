package org.gyfor.dao.registry;

import org.gyfor.dao.IDataTableReference;
import org.gyfor.dao.IDataTableReferenceRegistry;
import org.osgi.service.component.annotations.Component;


@Component
public class DataTableReferenceRegistry extends ServiceRegistry<IDataTableReference> implements IDataTableReferenceRegistry {

}
