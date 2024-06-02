import clsx from 'clsx'
import PropTypes from 'prop-types'

const ProductTop = ({ product, bgColor = 'white' }) => {
    return (
        <div style={{ backgroundColor: bgColor }} className='col-span-12 flex'>
            <div className={clsx('w-1/3 shrink-0')}>
                <img className={clsx('h-full object-cover object-right')} src={product.image} alt={product.name} />
            </div>
            <div className={clsx('ml-10 pr-28 flex flex-col justify-center flex-1')}>
                <h4 className='font-medium text-5xl'>{product.name}</h4>
                <p className='text-[#909090] text-sm mt-3'>{product.description}</p>
            </div>
        </div>
    )
}

export default ProductTop

ProductTop.propTypes = {
    product: PropTypes.shape({
        name: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        image: PropTypes.string.isRequired
    }),
    bgColor: PropTypes.string,
    positionImage: PropTypes.shape('left' | 'right'),
    textSize: PropTypes.string
}
