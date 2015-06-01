package com.yetu.oauth2provider.data.riak

import com.scalapenos.riak.{ RiakBucket, RiakClient }

trait RiakConnection {

  def port: Int
  def host: String
  def bucketName: String

  def riakClient: RiakClient = RiakClient(host, port)
  def bucket: RiakBucket = riakClient.bucket(bucketName)
}

