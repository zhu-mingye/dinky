/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { postAll } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { ActionType, ProColumns, ProDescriptions, ProTable } from '@ant-design/pro-components';
import { ProDescriptionsItemProps } from '@ant-design/pro-descriptions';
import { Drawer } from 'antd';
import { useRef, useState } from 'react';

export type SavePointData = {
  id: number;
  taskId: number;
  name: string;
  type: string;
  path: string;
  createTime: Date;
};

export const SavePoint = (props: { taskId: number }) => {
  const { taskId } = props;

  const [row, setRow] = useState<SavePointData>();
  const actionRef = useRef<ActionType>();
  actionRef.current?.reloadAndRest?.();

  const columns: ProDescriptionsItemProps<SavePointData>[] | ProColumns<SavePointData>[] = [
    {
      title: l('pages.task.savePointPath'),
      dataIndex: 'path',
      hideInForm: true,
      hideInSearch: true
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
      render: (dom: any, entity: SavePointData) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      }
    }
  ];

  return (
    <>
      <ProTable<SavePointData>
        className={'datastudio-theme'}
        actionRef={actionRef}
        rowKey='id'
        request={(params, sorter, filter) =>
          postAll(API_CONSTANTS.GET_SAVEPOINT_LIST, { ...params, sorter, filter })
        }
        params={{ taskId }}
        columns={columns as ProColumns<SavePointData>[]}
        search={false}
      />
      <Drawer
        width={600}
        open={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions<SavePointData>
            column={2}
            title={row?.name}
            request={async () => ({
              data: row || {}
            })}
            params={{
              id: row?.name
            }}
            columns={columns as ProDescriptionsItemProps<SavePointData>[]}
          />
        )}
      </Drawer>
    </>
  );
};
