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

import org.hibernate.dialect.PostgreSQLDialect;

public class PGSQLMapDialect extends PostgreSQLDialect {

//	protected String columnType(int sqlTypeCode) {
//		return Types.CLOB == sqlTypeCode || SqlTypes.LONG32VARBINARY == sqlTypeCode ? super.columnType(SqlTypes.LONG32VARCHAR) : super.columnType(sqlTypeCode);
//	}
//	
//	protected String castType(int sqlTypeCode) {
//		return Types.CLOB == sqlTypeCode || SqlTypes.LONG32VARBINARY == sqlTypeCode ? super.castType(SqlTypes.LONG32VARCHAR) : super.castType(sqlTypeCode);
//	}
}