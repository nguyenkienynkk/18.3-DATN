import { Form, Input, Button, Checkbox, DatePicker, InputNumber, Select, Card } from 'antd'
import 'tailwindcss/tailwind.css'
import useDiscountUpdateViewModel from './DiscountUpdate.vm'
import { DeleteOutlined } from '@ant-design/icons'

const DISCOUNT_TYPES = [
    { value: 0, label: 'Assigned to order total' },
    { value: 1, label: 'Assigned to products' },
    { value: 2, label: 'Assigned to categories' },
    { value: 3, label: 'Assigned to manufacturers' },
    { value: 4, label: 'Assigned to order subtotal' },
]

const DISCOUNT_LIMITATIONS = [
    { value: 0, label: 'Unlimited' },
    { value: 1, label: 'N times only' },
    { value: 2, label: 'N times per customer' },
]

export default function DiscountUpdate() {
    const {
        form,
        usePercentage,
        requiresCouponCode,
        discountLimitation,
        discountTypeId,
        handleSubmit,
        load,
        handleDelete,
        handleDiscountTypeChange,
        handleUsePercentageChange,
        handleRequiresCouponCodeChange,
        handleDiscountLimitationChange,
    } = useDiscountUpdateViewModel()

    return (
        <div className='p-6 bg-white rounded-md shadow-md'>
            <Form form={form} layout='vertical' onFinish={handleSubmit} className='space-y-4'>
                <Card title='Basic Information' className='mb-4'>
                    <Form.Item name='isActive' valuePropName='checked'>
                        <Checkbox>Is active</Checkbox>
                    </Form.Item>

                    <Form.Item
                        name='name'
                        label='Name'
                        rules={[{ required: true, message: 'Please input the discount name!' }]}
                    >
                        <Input size='large' />
                    </Form.Item>
                </Card>

                {/* Discount Type and Details */}
                <Card title='Discount Type and Details' className='mb-4'>
                    <Form.Item name='discountTypeId' label='Discount type' initialValue={discountTypeId}>
                        <Select
                            size='large'
                            showSearch
                            onChange={handleDiscountTypeChange}
                            filterOption={(input, option) =>
                                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                            }
                            options={DISCOUNT_TYPES}
                        />
                    </Form.Item>
                    {discountTypeId === 2 && (
                        <Form.Item name='appliedToSubCategories' valuePropName='checked'>
                            <Checkbox>Applied to subcategories</Checkbox>
                        </Form.Item>
                    )}

                    {(discountTypeId === 1 || discountTypeId === 2 || discountTypeId === 3) && (
                        <Form.Item
                            name='maxDiscountedQuantity'
                            label='Maximum discounted quantity'
                            rules={[{ required: true, message: 'Please input the maximum discounted quantity!' }]}
                        >
                            <InputNumber size='large' />
                        </Form.Item>
                    )}
                </Card>
                {/* Discount Values */}
                <Card title='Discount Values' className='mb-4'>
                    <Form.Item name='usePercentage' valuePropName='checked' initialValue={false}>
                        <Checkbox onChange={handleUsePercentageChange} checked={usePercentage}>
                            Use percentage
                        </Checkbox>
                    </Form.Item>
                    {usePercentage ? (
                        <Card className='p-4 mt-4' title='Percentage Details'>
                            <Form.Item
                                name='discountPercentage'
                                label='Discount percentage'
                                rules={[{ required: true, message: 'Please input the discount percentage!' }]}
                            >
                                <InputNumber size='large' />
                            </Form.Item>

                            <Form.Item
                                name='maxDiscountAmount'
                                label='Maximum discount amount'
                                rules={[{ required: true, message: 'Please input the maximum discount amount!' }]}
                            >
                                <InputNumber size='large' addonAfter='USD' />
                            </Form.Item>
                        </Card>
                    ) : (
                        <Card className='p-4 mt-4' title='Amount Details'>
                            <Form.Item
                                name='discountAmount'
                                label='Discount amount'
                                rules={[{ required: true, message: 'Please input the discount amount!' }]}
                            >
                                <InputNumber size='large' addonAfter='USD' />
                            </Form.Item>
                        </Card>
                    )}
                </Card>
                {/* Coupon and Dates */}
                <Card title='Coupon and Dates' className='mb-4'>
                    <Form.Item name='requiresCouponCode' valuePropName='checked' initialValue={false}>
                        <Checkbox onChange={handleRequiresCouponCodeChange} checked={requiresCouponCode}>
                            Requires coupon code
                        </Checkbox>
                    </Form.Item>
                    {requiresCouponCode && (
                        <Form.Item name='couponCode' label='Coupon code'>
                            <Input size='large' placeholder='Enter coupon code' />
                        </Form.Item>
                    )}
                    <div className='flex space-x-4'>
                        <Form.Item
                            name='startDate'
                            label='Start date'
                            rules={[{ required: true, message: 'Please select the start date!' }]}
                            style={{ flex: 1 }}
                        >
                            <DatePicker size='large' showTime style={{ width: '100%' }} />
                        </Form.Item>
                        <Form.Item
                            name='endDate'
                            label='End date'
                            rules={[{ required: true, message: 'Please select the end date!' }]}
                            style={{ flex: 1 }}
                        >
                            <DatePicker size='large' showTime style={{ width: '100%' }} />
                        </Form.Item>
                    </div>
                </Card>
                {/* Additional Information */}
                <Card title='Additional Information' className='mb-4'>
                    <Form.Item name='isCumulative' valuePropName='checked' initialValue={false}>
                        <Checkbox>Cumulative with other discounts</Checkbox>
                    </Form.Item>

                    <Form.Item
                        name='discountLimitationId'
                        label='Discount Limitation'
                        initialValue={discountLimitation}
                    >
                        <Select
                            size='large'
                            showSearch
                            onChange={handleDiscountLimitationChange}
                            filterOption={(input, option) =>
                                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                            }
                            options={DISCOUNT_LIMITATIONS}
                        />
                    </Form.Item>
                    {(discountLimitation === 1 || discountLimitation === 2) && (
                        <Form.Item
                            name='limitationTimes'
                            label='N times'
                            rules={[{ required: true, message: 'Please input the number of times!' }]}
                        >
                            <InputNumber size='large' />
                        </Form.Item>
                    )}
                    <Form.Item name='comment' label='Admin comment'>
                        <Input.TextArea size='large' placeholder='Add comments here...' />
                    </Form.Item>
                </Card>
                <Form.Item>
                    <Button size='large' type='primary' htmlType='submit' loading={load}>
                        Save
                    </Button>
                    <Button
                        type='default'
                        danger
                        size='large'
                        icon={<DeleteOutlined />}
                        onClick={handleDelete}
                        style={{ marginLeft: '10px' }}
                    >
                        Delete
                    </Button>
                </Form.Item>
            </Form>
        </div>
    )
}
