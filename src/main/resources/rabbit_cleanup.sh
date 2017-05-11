#!/usr/bin/env bash
echo "Existing rabbitmq queues"
rabbitmqadmin list queues name
echo "Deleting operinos queue"
rabbitmqadmin delete queue name='operinos'
echo "Deleted operinos queue"
echo "Rabbitmq queues after delete"
rabbitmqadmin list queues name