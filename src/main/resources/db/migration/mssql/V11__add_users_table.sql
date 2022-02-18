CREATE SEQUENCE [dbo].[users_sequence]
 AS [bigint]
 START WITH 1
 INCREMENT BY 1
 CACHE
GO
CREATE TABLE [dbo].[users](
	[id] [bigint] CONSTRAINT [PK__users] PRIMARY KEY,
	[account_id] [VARCHAR](36) CONSTRAINT [DF__users_account_id] NOT NULL DEFAULT '',
    [login] [VARCHAR](128) CONSTRAINT [DF__users_login] NOT NULL DEFAULT '',
	[password] [VARCHAR](128) CONSTRAINT [DF__users_password] NOT NULL DEFAULT '')
GO