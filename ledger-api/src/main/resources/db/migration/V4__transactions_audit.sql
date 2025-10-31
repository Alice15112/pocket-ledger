create table if not exists transactions_audit (
  id              bigserial primary key,
  event_id        uuid        not null,
  account_id      uuid        not null,
  tx_id           uuid        not null,
  amount_minor    bigint      not null,
  tx_type         varchar(16) not null,
  produced_at     timestamptz not null default now()
);

create unique index if not exists ux_transactions_audit_event on transactions_audit(event_id);
create index if not exists idx_transactions_audit_account on transactions_audit(account_id, produced_at desc);
