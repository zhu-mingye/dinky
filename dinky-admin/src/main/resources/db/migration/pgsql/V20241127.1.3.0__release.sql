
SELECT add_column_if_not_exists('public','dinky_alert_history', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_history', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_job_history', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_job_instance', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_sys_menu', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_sys_role_menu', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_row_permissions', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_user_role', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_user_tenant', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_udf_manage', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_udf_template', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_catalogue', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_cluster_configuration', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_cluster', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_dashboard', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_database', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_flink_document', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_fragment', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_git_project', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_metrics', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_sys_operate_log', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_resources', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_savepoints', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_sys_token', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_task', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_task_version', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_alert_group', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_alert_instance', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_alert_rules', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','dinky_alert_template', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

-- 删除 is_deleted 字段
ALTER TABLE public.dinky_alert_history DROP COLUMN IF EXISTS is_deleted;
SELECT add_column_if_not_exists('public','dinky_sys_login_log', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');

SELECT add_column_if_not_exists('public','metadata_column', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');
SELECT add_column_if_not_exists('public','metadata_database', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');
SELECT add_column_if_not_exists('public','metadata_database_property', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');
SELECT add_column_if_not_exists('public','metadata_function', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');
SELECT add_column_if_not_exists('public','metadata_table', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');
SELECT add_column_if_not_exists('public','metadata_table_property', 'is_delete', 'BOOLEAN', 'FALSE', 'is delete ');


CREATE UNIQUE INDEX unx_metadata_column ON metadata_column (table_id, column_name, is_delete);
CREATE UNIQUE INDEX unx_metadata_database ON metadata_database (database_name, is_delete);
CREATE UNIQUE INDEX unx_metadata_database_property ON metadata_database_property (database_id, key, is_delete);
CREATE UNIQUE INDEX unx_metadata_function ON metadata_function (function_name, database_id, is_delete);
CREATE UNIQUE INDEX unx_metadata_table ON metadata_table (table_name, database_id, is_delete);
CREATE UNIQUE INDEX unx_metadata_table_property ON metadata_table_property (table_id, key, is_delete);
