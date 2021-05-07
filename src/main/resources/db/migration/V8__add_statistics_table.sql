USE [KombineProxyServer]
GO
CREATE TABLE [dbo].[statistics_data](
	[date_time] [DATETIME2] PRIMARY KEY,
	[delayed_queries_count] [int] NOT NULL,
	[similar_queries_count] [int] NOT NULL,
	[delayed_queries_duration_in_ms] [int] NOT NULL)
GO