create table members (
                         id bigserial primary key,
                         first_name varchar(100) not null,
                         last_name varchar(100) not null,
                         birth_date date not null,
                         photo_url varchar(500),
                         active boolean not null,
                         created_at timestamptz not null,
                         updated_at timestamptz not null
);

create table roles (
                       id bigserial primary key,
                       name varchar(50) not null unique
);

create table user_accounts (
                               id bigserial primary key,
                               username varchar(100) not null unique,
                               email varchar(150) not null unique,
                               password_hash varchar(255) not null,
                               enabled boolean not null,
                               member_id bigint,
                               constraint fk_user_member
                                   foreign key (member_id) references members(id)
);

create table user_roles (
                            user_id bigint not null,
                            role_id bigint not null,
                            primary key (user_id, role_id),
                            constraint fk_user_roles_user foreign key (user_id) references user_accounts(id),
                            constraint fk_user_roles_role foreign key (role_id) references roles(id)
);

create table training_groups (
                                 id bigserial primary key,
                                 name varchar(120) not null unique,
                                 description varchar(1000),
                                 active boolean not null
);

create table training_sessions (
                                   id bigserial primary key,
                                   training_group_id bigint not null,
                                   title varchar(150) not null,
                                   description varchar(2000),
                                   location varchar(200),
                                   start_time timestamptz not null,
                                   end_time timestamptz not null,
                                   registration_deadline timestamptz,
                                   max_participants integer,
                                   waitlist_enabled boolean not null,
                                   status varchar(30) not null,
                                   constraint fk_session_group foreign key (training_group_id) references training_groups(id)
);

create table registrations (
                               id bigserial primary key,
                               training_session_id bigint not null,
                               member_id bigint not null,
                               status varchar(30) not null,
                               registered_at timestamptz not null,
                               cancelled_at timestamptz,
                               constraint fk_registration_session foreign key (training_session_id) references training_sessions(id),
                               constraint fk_registration_member foreign key (member_id) references members(id),
                               constraint uk_registration_session_member unique (training_session_id, member_id)
);