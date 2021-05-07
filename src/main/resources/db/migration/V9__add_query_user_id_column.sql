GO
USE [KombineProxyServer]
ALTER TABLE [dbo].[queries] ADD [message_user_id] [VARCHAR](36) CONSTRAINT [DF__queries_message_user_id] NOT NULL DEFAULT ''
GO