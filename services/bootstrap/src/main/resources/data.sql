-- 初始化  manager 的数据：
INSERT IGNORE INTO `manager` (`id`, `version`, `salt`, `security`, `status`, `user_name`, `create_time`) VALUES
  (1, 0, 'c40fff5e-53c8-481e-9775-3024bf32054d', 'd224a5ece25e9150fb4a458af6a1dfd2', 'ACTIVED', 'admin',
   now());
-- 初始化  permission 的数据：
INSERT INTO `permission`
SET `perm` = 'SuperVisor', `name` = '超级管理员', `o` = 0, `perm_group` = NULL
ON DUPLICATE KEY UPDATE `name` = '超级管理员', `o` = 0, `perm_group` = NULL;
-- 初始化  role 的数据：
INSERT INTO `role` (`role_name`, `create_time`, `role_text`, `super_visor`) VALUES
  ('SuperVisor', '2017-12-05 15:14:58', '超级管理员', b'1');

-- 初始化  roleperm 的数据：
INSERT INTO `roleperm` (`role`, `perm`) VALUES
  ('SuperVisor', 'SuperVisor');

-- 初始化  managerrole 的数据：
INSERT INTO `managerrole` (`managerid`, `role_name`) VALUES
  (1, 'SuperVisor');