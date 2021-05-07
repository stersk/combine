GO
USE [KombineProxyServer]
ALTER TABLE [dbo].[queries] ADD [message_type] [VARCHAR](15) CONSTRAINT [DF__queries_message_type] NOT NULL DEFAULT '',
                                [message_token] [VARCHAR](20) CONSTRAINT [DF__queries_message_token] NOT NULL DEFAULT ''
GO