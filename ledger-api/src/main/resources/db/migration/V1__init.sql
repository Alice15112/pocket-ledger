create type tx_status as enum ('PENDING','POSTED','FAILED');
create type hold_status as enum ('ACTIVE','CLOSED');

create table accounts (
  id uuid primary key,
  owner_id uuid,
  currency char(3) not null,
  status varchar(16) not null default 'ACTIVE',
  created_at timestamptz not null default now()
);

create table transactions (
  id uuid primary key,
  external_idempotency_key text unique,
  type varchar(32) not null,
  status tx_status not null,
  created_at timestamptz not null default now()
);

create table journal_entries (
  id bigserial primary key,
  tx_id uuid not null references transactions(id) on delete restrict,
  account_id uuid not null references accounts(id) on delete restrict,
  amount_minor bigint not null check (amount_minor > 0),
  dc char(1) not null check (dc in ('D','C')),
  created_at timestamptz not null default now()
);
create index idx_journal_tx on journal_entries(tx_id);
create index idx_journal_account on journal_entries(account_id, created_at);

create table holds (
  id uuid primary key,
  account_id uuid not null references accounts(id) on delete restrict,
  amount_minor bigint not null check (amount_minor > 0),
  status hold_status not null,
  expires_at timestamptz null,
  created_at timestamptz not null default now()
);
