insert into user_accounts (
    username,
    email,
    password_hash,
    enabled,
    member_id
) values (
             'admin',
             'admin@turnerplus.de',
             '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
             true,
             null
         );

insert into user_roles (user_id, role_id)
select ua.id, r.id
from user_accounts ua
         join roles r on r.name = 'ROLE_ADMIN'
where ua.username = 'admin';