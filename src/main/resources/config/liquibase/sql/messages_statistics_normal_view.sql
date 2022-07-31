CREATE VIEW messages_statistics_vw
AS
    SELECT s.date,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'EMAIL' AND s.status = 'SENT'), 0)            AS total_email_sent,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'EMAIL' AND s.status = 'FAILED'), 0)          AS total_email_failed,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'EMAIL' AND s.status = 'IN_QUEUE'), 0)        AS total_email_queue,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'SMS' AND s.status = 'SENT'), 0)              AS total_sms_sent,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'SMS' AND s.status = 'FAILED'), 0)            AS total_sms_failed,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'SMS' AND s.status = 'IN_QUEUE'), 0)          AS total_sms_queue,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'NOTIFICATION' AND s.status = 'SENT'), 0)     AS total_notification_sent,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'NOTIFICATION' AND s.status = 'FAILED'), 0)   AS total_notification_failed,
           COALESCE(SUM(s.total) FILTER(WHERE s.type = 'NOTIFICATION' AND s.status = 'IN_QUEUE'), 0) AS total_notification_queue
    FROM (SELECT CAST(created_at AS DATE) AS date, status, count(*) AS total, 'EMAIL' AS type
          FROM email
          GROUP BY CAST (created_at AS DATE), status
          UNION ALL
          SELECT CAST (created_at AS DATE) AS date, status, count (*) AS total, 'SMS' AS type
          FROM short_message
          GROUP BY CAST (created_at AS DATE), status
          UNION ALL
          SELECT CAST (created_at AS DATE) AS date, status, count (*) AS total, 'NOTIFICATION' AS type
          FROM notification
          GROUP BY CAST (created_at AS DATE), status) s
    WHERE s.date < CURRENT_DATE
      AND s.date >= CURRENT_DATE - 30
    GROUP BY s.date
    ORDER BY s.date DESC;
