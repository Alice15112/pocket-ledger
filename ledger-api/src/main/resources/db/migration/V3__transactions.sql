create table if not exists transactions
(
  id          uuid primary key,
  account_id  uuid           not null references accounts(id) on delete cascade,
  amount      numeric(19,2)  not null check (amount > 0),
  type        varchar(6)     not null check (type in ('CREDIT','DEBIT')),
  external_id varchar(64) unique,
  created_at  timestamptz    not null default now()
);

create index if not exists idx_tx_account_created
  on transactions(account_id, created_at desc);
