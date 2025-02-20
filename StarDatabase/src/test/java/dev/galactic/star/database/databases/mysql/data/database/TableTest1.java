/*
 * Copyright 2023 Galactic Star Studios
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.galactic.star.database.databases.mysql.data.database;

import dev.galactic.star.database.databases.mysql.data.MySqlDataTypes;
import dev.galactic.star.database.impl.annotations.TableColumn;

public class TableTest1 {

	@TableColumn(name = "count",
			maxDisplayed = 100,
			autoCreate = true,
			primaryKey = true,
			primaryKeyVal = "key_name",
			autoIncrement = true,
			notNull = true)
	String count = MySqlDataTypes.INT;

	@TableColumn(name = "user_names", maxDisplayed = 100, notNull = true)
	String usernames = MySqlDataTypes.VARCHAR;

	@TableColumn(name = "passwords", maxDisplayed = 100, notNull = true)
	String passwords = MySqlDataTypes.VARCHAR;
}
