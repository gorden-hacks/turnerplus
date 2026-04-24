create table trainer_group_assignments (
       id bigserial primary key,
       user_id bigint not null,
       training_group_id bigint not null,
       constraint fk_trainer_group_assignment_user
           foreign key (user_id) references user_accounts(id),
       constraint fk_trainer_group_assignment_group
           foreign key (training_group_id) references training_groups(id),
       constraint uk_trainer_group_assignment
           unique (user_id, training_group_id)
);