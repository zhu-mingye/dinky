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

import { Button, Flex, Input, Radio, Space, Typography } from 'antd';

import { WelcomPic1 } from '@/components/Icons/WelcomIcons';
import FormItem from 'antd/es/form/FormItem';
import { WelcomProps } from '@/pages/Other/Welcom';
import { l } from '@/utils/intl';

const { Title, Text, Link } = Typography;

const BaseConfigItem = (prop: WelcomProps) => {
  return (
    <Flex>
      <div>
        <Space>
          <Title>{l('welcom.base.config.title')}</Title>
        </Space>
        <br />
        <Text type={'success'}>{l('welcom.tips')}</Text>
        <br />
        <br />

        <FormItem label={l('welcom.base.config.dinky.url.title')}>
          <Text type={'danger'}>{l('welcom.base.config.dinky.url')}</Text>
          <FormItem name='sys.env.settings.dinkyAddr'>
            <Input />
          </FormItem>
        </FormItem>

        <FormItem label={l('welcom.base.config.taskowner.title')}>
          <Text type={'secondary'}>{l('welcom.base.config.taskowner')}</Text>
          <br />
          <FormItem name='sys.env.settings.taskOwnerLockStrategy'>
            <Radio.Group>
              <Radio.Button value='OWNER'>OWNER</Radio.Button>
              <Radio.Button value='OWNER_AND_MAINTAINER'>OWNER_AND_MAINTAINER</Radio.Button>
              <Radio.Button value='ALL'>ALL</Radio.Button>
            </Radio.Group>
          </FormItem>
        </FormItem>
        <Button type={'primary'} size={'large'} onClick={prop.onNext}>
          {l('welcom.next')}
        </Button>
        <Link onClick={prop.onPrev}> {l('welcom.prev')}</Link>
      </div>
      <WelcomPic1 size={500} />
    </Flex>
  );
};
export default BaseConfigItem;
