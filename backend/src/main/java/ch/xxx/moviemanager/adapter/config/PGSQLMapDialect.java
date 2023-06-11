/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.moviemanager.adapter.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

public class PGSQLMapDialect extends PostgreSQLDialect {

//	@Override
//	public boolean equivalentTypes(int typeCode1, int typeCode2) {
//		boolean result = super.equivalentTypes(typeCode1, typeCode2) || (SqlTypes.isCharacterOrClobType(typeCode1) && SqlTypes.isCharacterOrClobType(typeCode2));
//		return result;
//	}
//	
//	@Override
//	protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
//		super.registerColumnTypes(typeContributions, serviceRegistry);
//		final DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
//		ddlTypeRegistry.addDescriptor( new DdlTypeImpl( SqlTypes.VARCHAR, "clob", this ) );
//		ddlTypeRegistry.addDescriptor( new DdlTypeImpl( SqlTypes.VARCHAR, "text", this ) );
//	}
}