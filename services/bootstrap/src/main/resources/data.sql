-- 初始化  manager 的数据：
INSERT IGNORE INTO `manager` (`id`, `version`, `salt`, `security`, `status`, `userName`, `createTime`) VALUES
  (1, 0, 'c40fff5e-53c8-481e-9775-3024bf32054d', 'd224a5ece25e9150fb4a458af6a1dfd2', 'ACTIVED', 'admin',
   '2018-04-24 15:10:00');

-- 初始化  role 的数据：
INSERT IGNORE INTO `role` (`roleName`, `createTime`, `roleText`, `superVisor`) VALUES
  ('SuperVisor', '2017-12-05 15:14:58', '超级管理员', b'1');

INSERT INTO `Permission` (`perm`, `name`, `o`, `permGroup`) VALUES ('SuperVisor', 'SuperVisor', 0, NULL);
-- 初始化  roleperm 的数据：
INSERT IGNORE INTO `roleperm` (`role`, `perm`) VALUES
  ('SuperVisor', 'SuperVisor');

-- 初始化  managerrole 的数据：
INSERT IGNORE INTO `managerrole` (`managerID`, `roleName`) VALUES
  (1, 'SuperVisor');