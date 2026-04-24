create table member_group_permissions (
      id bigserial primary key,
      member_id bigint not null,
      training_group_id bigint not null,
      valid_from date not null,
      valid_to date,
      active boolean not null,
      constraint fk_member_group_permission_member
          foreign key (member_id) references members(id),
      constraint fk_member_group_permission_group
          foreign key (training_group_id) references training_groups(id),
      constraint uk_member_group_permission
          unique (member_id, training_group_id)
);