USE [KombineProxyServer]
GO
CREATE SEQUENCE [dbo].[repeated_query_sequence]
 AS [bigint]
 START WITH 1
 INCREMENT BY 1
 CACHE
GO
CREATE TABLE [dbo].[repeated_queries](
	[id] [bigint] CONSTRAINT [PK__repeated_queries] PRIMARY KEY,
	[date_time] [DATETIME2] CONSTRAINT [DF__repeated_queries_date_time] NOT NULL,
    [message_type] [VARCHAR](15) CONSTRAINT [DF__repeated_queries_message_type] NOT NULL DEFAULT '',
    [message_token] [VARCHAR](20) CONSTRAINT [DF__repeated_queries_message_token] NOT NULL DEFAULT '',
	[message_user_id] [VARCHAR](36) CONSTRAINT [DF__repeated_queries_message_user_id] NOT NULL DEFAULT '',
	[query_delay_in_ms] [int] CONSTRAINT [DF__repeated_queries_query_delay_in_ms] NOT NULL)
GO