ALTER TABLE accounts
  ALTER COLUMN currency TYPE varchar(3)
  USING trim(currency);
