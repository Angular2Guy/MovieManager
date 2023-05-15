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

import java.sql.Types;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.type.SqlTypes;

public class PGSQLMapDialect extends PostgreSQLDialect {

//	@Override
//	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
//		if (Types.CLOB == sqlTypeDescriptor.getSqlType() || Types.LONGVARCHAR == sqlTypeDescriptor.getSqlType()) {
//			return LongVarcharTypeDescriptor.INSTANCE;
//		}
//		return super.remapSqlTypeDescriptor(sqlTypeDescriptor);
//	}

	protected String columnType(int sqlTypeCode) {
		return Types.CLOB == sqlTypeCode ? super.columnType(SqlTypes.LONG32VARCHAR) : super.columnType(sqlTypeCode);
	}
	
	protected String castType(int sqlTypeCode) {
		return Types.CLOB == sqlTypeCode ? super.castType(SqlTypes.LONG32VARCHAR) : super.castType(sqlTypeCode);
	}
}