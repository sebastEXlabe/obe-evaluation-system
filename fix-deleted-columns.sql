-- Fix missing deleted columns for MyBatis-Plus @TableLogic
ALTER TABLE project_milestone ADD COLUMN IF NOT EXISTS deleted INT DEFAULT 0;
ALTER TABLE project_task ADD COLUMN IF NOT EXISTS deleted INT DEFAULT 0;
ALTER TABLE project_journal ADD COLUMN IF NOT EXISTS deleted INT DEFAULT 0;
ALTER TABLE contribution_log ADD COLUMN IF NOT EXISTS deleted INT DEFAULT 0;
ALTER TABLE repo_config ADD COLUMN IF NOT EXISTS deleted INT DEFAULT 0;

-- Verify
SELECT table_name, column_name FROM information_schema.columns
WHERE table_schema = 'public' AND column_name = 'deleted'
ORDER BY table_name;
